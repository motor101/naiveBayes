import java.util.*;

enum VOTING_DECISION {
    YES,
    NO,
    UNKNOWN_DISPOSITION,
    VOTING_DECISION_COUNT
}

enum MEMBER_TYPE {
    DEMOCRAT,
    REPUBLICAN,
    MEMBER_TYPE_COUNT
}

class ParliamentMember {
    ParliamentMember(MEMBER_TYPE memberType, VOTING_DECISION[] attributesArr) {
        this.memberType = memberType;
        this.attributesArr = Arrays.copyOf(attributesArr, attributesArr.length);
    }

    @Override
    public String toString() {
        return "ParliamentMember{" +
                "memberType=" + memberType +
                ", attributesArr=" + Arrays.toString(attributesArr) +
                '}';
    }

    MEMBER_TYPE memberType;
    VOTING_DECISION[] attributesArr;
}

public class Main {
    private static Random random = new Random();
    private static Scanner input = new Scanner(System.in);

    private ParliamentMember[] membersArr;
    private int membersCount;
    private int groupsCount;
    private int attributesCount;
    private SampleGroup[] groupArr;
    private Model model;

    private Main(ParliamentMember[] membersArr, int groupsCount, int attributesCount) {
        this.membersArr = Arrays.copyOf(membersArr, membersArr.length);
        this.membersCount = membersArr.length;
        this.groupsCount = groupsCount;
        this.attributesCount = attributesCount;

        groupArr = new SampleGroup[groupsCount];
        generateGroups();

        model = new Model(groupArr, attributesCount);
    }

    private void generateGroups() {
        final int groupSize = membersCount / groupsCount;
        int firstIndex = 0;
        int lastIndex = groupSize - 1;

        for (int i = 0; i < groupsCount; i++) {
            if (i == (groupsCount - 1)) {
                lastIndex = membersCount - 1;
            }

            groupArr[i] = new SampleGroup(membersArr, firstIndex, lastIndex, attributesCount);

            firstIndex = lastIndex + 1;
            lastIndex += groupSize;
        }

    }

    static private void randomizeSample(ParliamentMember[] membersArr) {
        ParliamentMember tmp;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < membersArr.length; i++) {
                int newIndex = random.nextInt(membersArr.length);

                tmp = membersArr[i];
                membersArr[i] = membersArr[newIndex];
                membersArr[newIndex] = tmp;
            }
        }
    }

    static private ParliamentMember[] getParliamentMembers(int membersCount, int attributesCount, int totalAttributesCount) {
        ParliamentMember[] members = new ParliamentMember[membersCount];
        MEMBER_TYPE memberType;
        VOTING_DECISION[] attributesArr = new VOTING_DECISION[attributesCount];

        for (int i = 0; i < membersCount; i++) {
            if (input.next().equals("democrat")) {
                memberType = MEMBER_TYPE.DEMOCRAT;
            } else {
                memberType = MEMBER_TYPE.REPUBLICAN;
            }

            for (int j = 0; j < totalAttributesCount; j++) {
                String str = input.next();
                if (j < attributesCount) {
                    switch (str) {
                        case "y":
                            attributesArr[j] = VOTING_DECISION.YES;
                            break;
                        case "n":
                            attributesArr[j] = VOTING_DECISION.NO;
                            break;
                        default:
                            attributesArr[j] = VOTING_DECISION.UNKNOWN_DISPOSITION;
                            break;
                    }
                }
            }
            members[i] = new ParliamentMember(memberType, attributesArr);
        }

        return members;
    }

    private void kFoldCrossValidation() {
        double scoreSum = 0.0;

        for (int i = 0; i < groupsCount; i++) {
            model.setTestingGroupIndex(i);

            double score = model.getScore();
            scoreSum += score;

            System.out.println((i + 1) + ": " + score);
        }

        System.out.println("average score = " + (scoreSum / groupsCount));
    }


    public static void main(String[] args) {
        int groupsCount = Integer.parseInt(args[0]);
        int membersCount = input.nextInt();
        int attributesCount = input.nextInt();
        int totalAttributesCount = input.nextInt();

        ParliamentMember[] parliamentMembers = getParliamentMembers(membersCount, attributesCount, totalAttributesCount);

        randomizeSample(parliamentMembers);

        Main naiveBayes = new Main(parliamentMembers, groupsCount, attributesCount);

        naiveBayes.kFoldCrossValidation();
    }
}
