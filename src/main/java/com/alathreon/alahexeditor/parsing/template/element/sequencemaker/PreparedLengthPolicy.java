package com.alathreon.alahexeditor.parsing.template.element.sequencemaker;

import com.alathreon.alahexeditor.util.ByteView;

public record PreparedLengthPolicy(ByteView leftover, LengthPolicyCondition condition) {
}
