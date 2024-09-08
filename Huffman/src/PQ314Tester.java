import java.util.ArrayList;

public class PQ314Tester {

    public static void main(String[] args) {
//        PriorityQueue314<TreeNode> test = new PriorityQueue314<>();
//        test.enqueue(new TreeNode(1, 1));
//        test.enqueue(new TreeNode(2, 3));
//        test.enqueue(new TreeNode(0, 1));
//        test.enqueue(new TreeNode(-5, 3));
//        System.out.println(test.toString());
//        test.enqueue(test.dequeue());
//        System.out.println(test.toString());
        PriorityQueue314<Integer> test = new PriorityQueue314<>();
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nums.add((int) (Math.random() * 100));
        }
        for (int i = 0; i < nums.size(); i++) {
            test.enqueue(nums.get(i));
        }
        System.out.println(test.toString());
    }

}
