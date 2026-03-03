package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BattleEngine {

    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {

        if (teamA == null || teamB == null || teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Teams must not be null or empty");
        }

        // 🔹 IMPORTANT: create defensive copies
        teamA = new ArrayList<>(teamA);
        teamB = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        int rounds = 0;

        result.addLog("=== ENCOUNTER START ===");

        while (!teamA.isEmpty() && !teamB.isEmpty()) {
            rounds++;
            result.addLog("Round " + rounds);

            performTeamAttack(teamA, teamB, result);

            // remove defeated from teamB
            teamB.removeIf(c -> !c.isAlive());

            if (teamB.isEmpty()) break;

            performTeamAttack(teamB, teamA, result);

            // remove defeated from teamA
            teamA.removeIf(c -> !c.isAlive());

            result.addLog("");
        }

        String winner = teamA.isEmpty() ? "Team B" : "Team A";

        result.setWinner(winner);
        result.setRounds(rounds);
        result.addLog("Winner: " + winner);
        result.addLog("=== ENCOUNTER END ===");

        return result;
    }

    private void performTeamAttack(List<Combatant> attackers,
                                   List<Combatant> defenders,
                                   EncounterResult result) {

        for (Combatant attacker : attackers) {

            if (!attacker.isAlive()) continue;
            if (defenders.isEmpty()) break;

            Combatant target = defenders.get(random.nextInt(defenders.size()));

            int baseDamage = attacker.getAttackPower();

            int damage = (int) Math.max(1,
                    Math.round(baseDamage * (0.8 + random.nextDouble() * 0.4)));

            target.takeDamage(damage);

            result.addLog(attacker.getName()
                    + " hits "
                    + target.getName()
                    + " for "
                    + damage);
        }
    }
}