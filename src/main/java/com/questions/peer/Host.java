package com.questions.peer;

import com.questions.CommandOuterClass;
import com.questions.quesionnaire.Questionnaire;
import com.questions.quesionnaire.Round;
import com.questions.red.Neighbour;
import com.questions.utils.Command;
import com.questions.utils.Console;
import com.questions.quesionnaire.Question;

import java.util.*;

public class Host extends PeerListener {

    public Client clientUI;

    private final Peer peer;
    private final List<Question> questions;
    private final Set<Neighbour> participants;

    private Questionnaire<Neighbour> questionnaire;
    private Round<Neighbour> actualRound;
    private final HashMap<String, Command> commands;

    public Host(Peer peer, List<Question> questions) {
        this.peer = peer;
        this.questions = questions;
        this.participants = new HashSet<>();
        this.commands = new HashMap<>();
    }

    public void start() {
        Console.println("mode host started");

        this.peer.listener = this;

        Map<String, Command> commands = new HashMap<>();
        commands.put("connect", this.peer::actionConnect);
        commands.put("connected", args -> this.peer.actionShowConnected());
        commands.put("startQuestionnaire", args -> this.startQuestionnaire());

        Console.commander(commands);
    }

    private void startQuestionnaire() {
        this.questionnaire = new Questionnaire<>(this.questions, this.participants);

        this.commands.clear();
        this.commands.put("notifyPeers", args -> this.peer.sendRoundRequest(this.questionnaire.id));
        this.commands.put("participants", args -> this.showParticipants());
        this.commands.put("start", args -> this.nextQuestion());

        Console.commander(this.commands);
    }

    private void showParticipants() {
        Console.println("peers connected:");
        for (Neighbour participant: this.participants) {
            Console.println("\t"+participant.getAlias());
        }
    }

    private void showAnswers() {
        List<Round<Neighbour>> rounds = this.questionnaire.getRounds();

        List<String> options = new ArrayList<>();
        rounds.forEach(round -> options.add(round.question.title));

        int selected = Console.selectWithIndex("select the question:", options);

        StringBuilder text = new StringBuilder("answers:");
        for (Neighbour neighbour: rounds.get(selected).getAnswers().keySet()) {
            text.append("\t").append(neighbour.getAlias());
            text.append("\n\t\tanswer: ");
            text.append(String.join(",", rounds.get(selected).getAnswers().get(neighbour)));
        }

        Console.println(text.toString());
    }

    private void showActualAnswers() {
        Console.println("answers:");
        this.actualRound.getAnswers().forEach((neighbour, answers) -> {
            String text = "\t" + neighbour.getAlias();
            text += "\n\t\tanswer: " + String.join(",", answers);
            text += "\n\t\tcorrect: " + (this.actualRound.question.isCorrect(answers) ? "yes" : "no");
            text += "\n\t\taddPoint: " + (this.actualRound.getWinner() == neighbour ? "yes" : "no");

            Console.println(text);
        });
    }

    private void nextQuestion() {
        if (this.questionnaire.hasWinner()) {
            Console.println("finish round, have a winner: " + this.questionnaire.getWinner().getAlias());
            return;
        }

        this.actualRound = this.questionnaire.newRound();

        Console.print("sending question: " + this.actualRound.question.title + " -> ");
        this.peer.sendQuestion(this.actualRound.question);
        Console.println("successfully");

        this.commands.clear();
        this.commands.put("participants", args -> this.showParticipants());
        this.commands.put("answers", args -> this.showActualAnswers());
        this.commands.put("allAnswers", args -> this.showAnswers());
        this.commands.put("refresh", args -> {});
    }

    private void sendResults() {
        this.peer.sendAnswerResult(this.actualRound);

        this.commands.clear();
        this.commands.put("participants", args -> this.showParticipants());
        this.commands.put("allAnswers", args -> this.showAnswers());
        this.commands.put("answers", args -> this.showActualAnswers());
        this.commands.put("refresh", args -> {});

        if (this.questionnaire.hasWinner()) {
            this.commands.put("finish", args -> this.finishRound());
        } else {
            this.commands.put("nextQuestion", args -> this.nextQuestion());
        }
    }

    private void finishRound() {
        if (!this.questionnaire.hasWinner()) {
            if (Console.select("not have a winner, finish?", new String[]{"yes", "no"}) == 1) return;
            this.questionnaire.forceWinner();
        }

        this.commands.clear();
        this.commands.put("participants", args -> this.showParticipants());
        this.commands.put("allAnswers", args -> this.showAnswers());

        this.peer.sendRoundResult(this.questionnaire.getWinner());
    }

    @Override
    public void cmdAnswer(Neighbour neighbour, CommandOuterClass.Answer cmd) {
        if (this.actualRound.question.id == cmd.getQuestionId()) {
            this.actualRound.response(neighbour, cmd.getDataList());
        } else {
            for (Round<Neighbour> round: this.questionnaire.getRounds()) {
                if (round.question.id == cmd.getQuestionId()) {
                    round.response(neighbour, cmd.getDataList());
                    break;
                }
            }
        }

        if (this.actualRound.hasWinner()) {
            this.commands.put("sendResults", args -> this.sendResults());
        }
    }

    @Override
    void cmdRoundResponse(Neighbour neighbour, CommandOuterClass.RoundResponse cmd) {
        if (cmd.getOk() && this.actualRound == null) this.participants.add(neighbour);
    }
}
