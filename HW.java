import java.util.Stack;

public class HW {

  .
    static class Node {
        String value;
        Node left;
        Node right;

        public Node(String item) {
            value = item;
            left = null;
            right = null;
        }
    }

.
    public static boolean isOperator(String value) {
        return value.equals("+") ||
               value.equals("-") ||
               value.equals("*") ||
               value.equals("/");
    }

    
    public static Node buildTree(String postfix) {

        Stack<Node> stack = new Stack<>();

        // Split the postfix expression
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {

            // If it is a number, push it into the stack
            if (!isOperator(token)) {
                stack.push(new Node(token));
            }
            else {
                // Pop two nodes
                Node right = stack.pop();
                Node left = stack.pop();

                // Create a new operator node
                Node newNode = new Node(token);

                // Connect left and right children
                newNode.left = left;
                newNode.right = right;

                // Push the new tree back to stack
                stack.push(newNode);
            }
        }

        // The remaining node is the root
        return stack.pop();
    }

    // 2. Evaluate Expression Tree
    public static int evaluate(Node root) {

        // If root is empty
        if (root == null) {
            return 0;
        }

        // If it is a number, return it
        if (!isOperator(root.value)) {
            return Integer.parseInt(root.value);
        }

        // Evaluate left and right subtrees
        int leftValue = evaluate(root.left);
        int rightValue = evaluate(root.right);

        // Perform the operation
        switch (root.value) {

            case "+":
                return leftValue + rightValue;

            case "-":
                return leftValue - rightValue;

            case "*":
                return leftValue * rightValue;

            case "/":
                return leftValue / rightValue;

            default:
                return 0;
        }
    }

    // 3. Print Infix Expression
    public static void printInfix(Node root) {

        if (root == null) {
            return;
        }

        // Add opening bracket for operators
        if (isOperator(root.value)) {
            System.out.print("(");
        }

        // Print left subtree
        printInfix(root.left);

        // Print current node
        System.out.print(root.value);

        // Print right subtree
        printInfix(root.right);

        // Add closing bracket for operators
        if (isOperator(root.value)) {
            System.out.print(")");
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        // Test Case
        String postfix = "5 3 + 8 2 - *";

        // Build Expression Tree
        Node root = buildTree(postfix);

        // Print Infix Expression
        System.out.print("Infix: ");
        printInfix(root);

        // Print the result
        System.out.println();
        System.out.println("Result: " + evaluate(root));
    }
}
