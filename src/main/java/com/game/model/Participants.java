package com.game.model;

import java.util.List;

public enum Participants {
    SELF            //ownerPlayer == actor
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                return ownerPlayer.equals(actor);
            }
            public boolean isPossibleToPlayBoth() {
                return false;
            }
        },
    SELF_OR_BOTH    //ownerPlayer == actor || both
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                //ownerPlayer == actor || both
                return ownerPlayer.equals(actor) ;
            }
            public boolean isPossibleToPlayBoth() {
                return true;
            }

            @Override
            public List<Participants> getOptions() {
                return List.of(SELF, BOTH);
            }
        },
    PARTNER         //ownerPlayer != actor
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                //ownerPlayer != actor
                return !ownerPlayer.equals(actor);
            }
            public boolean isPossibleToPlayBoth() {
                return false;
            }
        },
    PARTNER_OR_BOTH  //ownerPlayer != actor || both
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                //ownerPlayer != actor || both
                return !ownerPlayer.equals(actor);
            }
            public boolean isPossibleToPlayBoth() {
                return true;
            }

            @Override
            public List<Participants> getOptions() {
                return List.of(PARTNER, BOTH);
            }
        },
    ANY_PERSON         // true && !both
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                // true && !both
                return true;
            }
            public boolean isPossibleToPlayBoth() {
                return false;
            }

            @Override
            public List<Participants> getOptions() {
                return List.of(SELF, PARTNER);
            }
        },
    ANY_PERSON_OR_BOTH   // true
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                return true;
            }
            public boolean isPossibleToPlayBoth() {
                return true;
            }

            @Override
            public List<Participants> getOptions() {
                return List.of(SELF, PARTNER, BOTH);
            }
        },
    BOTH               //both
        {
            public boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) {
                return false;
            }
            public boolean isPossibleToPlayBoth() {
                return true;
            }
        };

    public abstract boolean isPossibleToPlayByActor(Player ownerPlayer, Player actor) ;

    public abstract boolean isPossibleToPlayBoth();

    public  List<Participants> getOptions(){
        return List.of(this);
    }
}
