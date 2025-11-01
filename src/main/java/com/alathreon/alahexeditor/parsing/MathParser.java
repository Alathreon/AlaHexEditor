package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.UnionData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.util.DataSegment;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;

public class MathParser {
    private MathParser() {}

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern OP_PATTERN = Pattern.compile("^[+\\-*/%]$");

    private static long op(char op, long left, long right) {
        return switch (op) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            case '/' -> left / right;
            case '%' -> left % right;
            default -> throw new IllegalArgumentException("Illegal char %c".formatted(op));
        };
    }
    private static long parse(String s, boolean signed) {
        return signed ? Long.parseLong(s) : Long.parseUnsignedLong(s);
    }
    public static long evalPostfix(String expression, boolean signed, ParseObjects objects, DataSegment segment, long value) throws ParseException {
        objects.startScope();
        objects.add("x", new ParseObject(segment, new IntData(value, signed)));
        try {
            return internalEvalPostfix(expression, signed, objects, segment);
        } finally {
            objects.endScope();
        }
    }
    private static long internalEvalPostfix(String expression, boolean signed, ParseObjects objects, DataSegment segment) throws ParseException {
        Deque<Long> stack = new ArrayDeque<>();
        for (String element : expression.split(" ")) {
            if(OP_PATTERN.matcher(element).matches()) {
                if(stack.size() < 2) throw new ParseException(segment, "Only one operand for operator %s, for expression %s".formatted(element, expression));
                long right = stack.pop();
                long left = stack.pop();
                long result = op(element.charAt(0), left, right);
                stack.push(result);
            } else if(INT_PATTERN.matcher(element).matches()) {
                stack.push(parse(element, signed));
            } else if(Parser.IDENTIFIER_PATTERN.matcher(element).matches()) {
                ParseObject parseObject = objects.get(element);
                if(parseObject == null) throw new ParseException(segment, "Variable %s doesn't exist in expression %s".formatted(element, expression));
                switch (parseObject.data()) {
                    case IntData i -> stack.push(i.raw());
                    case EnumData e -> stack.push((long)e.code());
                    case UnionData u -> stack.push((long)u.intClassifier());
                    default -> throw new ParseException(segment, "Unsupported type %s for variable %s in expression %s".formatted(parseObject.data().type(), element, expression));
                }
            } else {
                throw new ParseException(segment, "Illegal element %s in expression %s".formatted(element, expression));
            }
        }
        if(stack.size() != 1) throw new ParseException(segment, "Invalid number of operators for expression %s".formatted(expression));
        return stack.pop();
    }
}
