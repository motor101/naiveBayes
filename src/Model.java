import java.awt.desktop.SystemEventListener;

class Model {
    private int[][][] attributesOccurrence;
    private int[] parliamentMembersOccurrence;

    private double[][][] attributesProbabilities;
    private double[] parliamentMembersProbabilities;

    private int testingGroupIndex;

    private final int attributesCount;
    private final SampleGroup[] groupArr;
    private final int groupsCount;

    Model(SampleGroup[] groupArr, int attributesCount) {
        this.groupArr = groupArr;
        groupsCount = groupArr.length;
        this.attributesCount = attributesCount;

        attributesOccurrence = new int[attributesCount][VOTING_DECISION.VOTING_DECISION_COUNT.ordinal()]
                [MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];
        parliamentMembersOccurrence = new int[MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];

        attributesProbabilities = new double[attributesCount][VOTING_DECISION.VOTING_DECISION_COUNT.ordinal()]
                [MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];
        parliamentMembersProbabilities = new double[MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];
    }

    void setTestingGroupIndex(int testingGroupIndex) {
        this.testingGroupIndex = testingGroupIndex;
    }

    double getScore() {
        calculateOccurrences();
        calculateProbabilities();

        SampleGroup testGroup = groupArr[testingGroupIndex];

        int correct = 0;

        for (int i = testGroup.firstIndex; i <= testGroup.lastIndex; i++) {
            ParliamentMember parliamentMemberForTesting = testGroup.membersArr[i];

            int mostProbableMemberTypeOrdinal = getMostProbableMemberTypeOrdinal(parliamentMemberForTesting);

            if (mostProbableMemberTypeOrdinal == parliamentMemberForTesting.memberType.ordinal()) {
                ++correct;
            }
        }

        int total = (testGroup.lastIndex - testGroup.firstIndex) + 1;

        return correct / (double) total;
    }

    private int getMostProbableMemberTypeOrdinal(ParliamentMember member) {
        int mostProbableTypeOrdinal = 0;
        double maxProbability = 0;

        for (int i = 0; i < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); i++) {
            double probability = getMemberTypeProbability(member, i);
            if (probability > maxProbability) {
                maxProbability = probability;
                mostProbableTypeOrdinal = i;
            }
        }

        return mostProbableTypeOrdinal;
    }

    private double getMemberTypeProbability(ParliamentMember member, int memberTypeOrdinal) {
        double probability = parliamentMembersProbabilities[memberTypeOrdinal];

        for (int i = 0; i < member.attributesArr.length; i++) {
            probability *= attributesProbabilities[i][member.attributesArr[i].ordinal()][memberTypeOrdinal];
        }

        return probability;
    }

    private void clearOccurrencesStatistics() {
        for (int j = 0; j < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); j++) {
            parliamentMembersOccurrence[j] = 0;
        }

        for (int j = 0; j < attributesCount; j++) {
            for (int k = 0; k < VOTING_DECISION.VOTING_DECISION_COUNT.ordinal(); k++) {
                for (int l = 0; l < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); l++) {
                    attributesOccurrence[j][k][l] = 0;
                }
            }
        }
    }

    private void calculateOccurrences() {
        clearOccurrencesStatistics();

        for (int i = 0; i < groupsCount; i++) {
            if (i == testingGroupIndex) {
                continue;
            }

            for (int j = 0; j < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); j++) {
                parliamentMembersOccurrence[j] += groupArr[i].parliamentMembersOccurrence[j];
            }

            for (int j = 0; j < attributesCount; j++) {
                for (int k = 0; k < VOTING_DECISION.VOTING_DECISION_COUNT.ordinal(); k++) {
                    for (int l = 0; l < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); l++) {
                        attributesOccurrence[j][k][l] += groupArr[i].attributesOccurrence[j][k][l];
                    }
                }
            }
        }
    }

    private void calculateProbabilities() {
        int membersCount = 0;
        for (int j = 0; j < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); j++) {
            membersCount += parliamentMembersOccurrence[j];
        }

        for (int j = 0; j < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); j++) {
            parliamentMembersProbabilities[j] = parliamentMembersOccurrence[j] / (double) membersCount;
        }

        for (int j = 0; j < attributesCount; j++) {
            for (int k = 0; k < VOTING_DECISION.VOTING_DECISION_COUNT.ordinal(); k++) {
                for (int l = 0; l < MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal(); l++) {
                    attributesProbabilities[j][k][l] =
                            attributesOccurrence[j][k][l] / (double) parliamentMembersOccurrence[l];
                }
            }
        }
    }
}
