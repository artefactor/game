package com.branka;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordCard {

    private Integer id;
    private Character group;
    private Character groupTone;
    private Tone tone;
    private String type;
    private Integer option;
    private String name;
    private List<Addition> additions;
    private Addition multiAddition;
    private boolean skip;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Addition {

        List<String> list;

        static Addition add(Type word) {
            return new Addition(List.of(word.name()));
        }

        static Addition add(Type word1, Type word2) {
            return new Addition(List.of(word1.name(), word2.name()));
        }
    }

    @NoArgsConstructor
    public static class MultiAddition extends WordCard.Addition {

        public MultiAddition(List<String> list) {
            super(list);
        }

        static Addition addMulti(Type word) {
            return new MultiAddition(List.of(word.name()));
        }
    }


    public enum Type {
        ГЛАГОЛ,
        НАРЕЧИЕ,
        НА_ЧТО,
        КОГО,
        ЧТО,
        ОБСТОЯТЕЛЬСТВО,
        НАД_КЕМ,
        НАД_ЧЕМ,
        ЧЕМ,
        КОМУ,
        КАКОМУ,
        СТЕПЕНЬ,
    }

    public enum Tone {
        RED(null),
        GREEN(null),
        YELLOW(null),
        NEUTRAL(null),
        ADDITIONAL(null),
        // pairs
        RED_OR_YELLOW(new Tone[] {RED, YELLOW}),
        NEUTRAL_OR_YELLOW(new Tone[] {NEUTRAL, YELLOW}),
        GREEN_OR_YELLOW(new Tone[] {GREEN, YELLOW}),
        RED_OR_NEUTRAL(new Tone[] {RED, NEUTRAL}),
        NEUTRAL_OR_GREEN(new Tone[] {GREEN, NEUTRAL}),
        RED_OR_GREEN(new Tone[] {RED, GREEN}),
        ;

        @Getter
        Tone[] doubles;

        Tone(Tone[] doubles) {
            this.doubles = doubles;
        }

        public boolean isDouble() {
            return doubles != null;
        }

    }

}
