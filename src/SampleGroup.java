class SampleGroup {
    SampleGroup(ParliamentMember[] membersArr, int firstIndex, int lastIndex, int attributesCount) {
        this.membersArr = membersArr;
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        attributesOccurrence = new int[attributesCount][VOTING_DECISION.VOTING_DECISION_COUNT.ordinal()]
                [MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];
        parliamentMembersOccurrence = new int[MEMBER_TYPE.MEMBER_TYPE_COUNT.ordinal()];

        calculateOccurrences();
    }

    private void calculateOccurrences() {
        ParliamentMember currentMember;

        for (int i = firstIndex; i <= lastIndex; i++) {
            currentMember = membersArr[i];

            ++parliamentMembersOccurrence[currentMember.memberType.ordinal()];

            for (int j = 0; j < currentMember.attributesArr.length; j++) {
                ++attributesOccurrence[j][currentMember.attributesArr[j].ordinal()][currentMember.memberType.ordinal()];
            }
        }
    }

    @Override
    public String toString() {
        return "SampleGroup{" +
                "firstIndex=" + firstIndex +
                ", lastIndex=" + lastIndex +
                '}';
    }

    final ParliamentMember[] membersArr;
    int[][][] attributesOccurrence;
    int[] parliamentMembersOccurrence;
    int firstIndex;
    int lastIndex;
}