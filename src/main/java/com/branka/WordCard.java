package com.branka;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordCard {

    private Integer id;
    private Tone tone;
    private Type type;
    private Integer option;
    private String name;
    private List<Addition> additions;
    private Addition multiAddition;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Addition {

        List<Type> list;

        static Addition add(Type word) {
            return new Addition(List.of(word));
        }

        static Addition add(Type word1, Type word2) {
            return new Addition(List.of(word1, word2));
        }
    }

    @NoArgsConstructor
    public static class MultiAddition extends WordCard.Addition {

        public MultiAddition(List<WordCard.Type> list) {
            super(list);
        }

        static Addition addMulti(Type word) {
            return new MultiAddition(List.of(word));
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
        RED,
        GREEN,
        YELLOW,
        NEUTRAL,
        ADDITIONAL,
    }

    //    @JsonIgnore
    //    boolean isActive;


}
