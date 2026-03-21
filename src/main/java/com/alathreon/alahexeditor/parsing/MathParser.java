package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.UnionData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.util.ByteView;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;

public class MathParser {
    private MathParser() {}

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern OP_PATTERN = Pattern.compile("^[+\\-*/%]$");

    private static BigInteger op(char op, BigInteger left, BigInteger right, boolean signed, int size) {
        return IntData.constraints(switch (op) {
            case '+' -> left.add(right);
            case '-' -> left.subtract(right);
            case '*' -> left.multiply(right);
            case '/' -> left.divide(right);
            case '%' -> left.mod(right);
            case '&' -> left.and(right);
            case '|' -> left.or(right);
            case '^' -> left.xor(right);
            case '<' -> left.shiftLeft(right.intValue());
            case '>' -> left.shiftRight(right.intValue());
            default -> throw new IllegalArgumentException("Illegal char %c".formatted(op));
        }, signed, size);
    }
    public static IntData evalPostfix(String expression, ParseObjects objects, ByteView segment, IntData value) throws ParseException {
        objects.startScope();
        objects.add("x", new ParseObject(segment, value));
        try {
            return new IntData(internalEvalPostfix(expression, value.signed(), value.size(), objects, segment), value.signed(), value.size());
        } finally {
            objects.endScope();
        }
    }
    private static BigInteger internalEvalPostfix(String expression, boolean signed, int size, ParseObjects objects, ByteView segment) throws ParseException {
        Deque<BigInteger> stack = new ArrayDeque<>();
        for (String element : expression.split(" ")) {
            if(OP_PATTERN.matcher(element).matches()) {
                if(stack.size() < 2) throw new ParseException(segment, objects, "Only one operand for operator %s, for expression %s".formatted(element, expression));
                BigInteger right = stack.pop();
                BigInteger left = stack.pop();
                BigInteger result = op(element.charAt(0), left, right, signed, size);
                stack.push(result);
            } else if(INT_PATTERN.matcher(element).matches()) {
                stack.push(IntData.constraints(new BigInteger(element), signed, size));
            } else if(Parser.IDENTIFIER_PATTERN.matcher(element).matches()) {
                ParseObject parseObject = objects.get(element);
                if(parseObject == null) throw new ParseException(segment, objects, "Variable %s doesn't exist in expression %s".formatted(element, expression));
                switch (parseObject.data()) {
                    case IntData i -> stack.push(i.value());
                    case EnumData e -> stack.push(BigInteger.valueOf(e.code()));
                    case UnionData u -> stack.push(u.intClassifier().value());
                    default -> throw new ParseException(segment, objects, "Unsupported type %s for variable %s in expression %s".formatted(parseObject.data().type(), element, expression));
                }
            } else {
                throw new ParseException(segment, objects, "Illegal element %s in expression %s".formatted(element, expression));
            }
        }
        if(stack.size() != 1) throw new ParseException(segment, objects, "Invalid number of operators for expression %s".formatted(expression));
        return stack.pop();
    }
}
