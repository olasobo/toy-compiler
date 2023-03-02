public class Machine {

    private static int logicalResult(int value) {
        int result = mask(value);
        Register.Flags.carry = false;
        Register.Flags.zero = (result == 0);
        Register.Flags.negative = (result > 0x7FFF);
        return result;
    }

    private static int arithmeticResult(int value) {
        int result = mask(value);
        Register.Flags.carry = value > 0x0000FFFF || value <= 0xFFFF0000;
        Register.Flags.negative = (result > 0x7FFF);
        Register.Flags.zero = (result == 0);
        return result;
    }

    private static int divisionByZero() {
        int result = 0x8000;
        Register.Flags.carry = true;
        Register.Flags.negative = true;
        Register.Flags.zero = false;
        return result;
    }

    private static int mask(int value) {
        return value & 0xFFFF;
    }

    private static boolean isNegative(int value) {
        return value > 0x7FFF;
    }

    private static int onesComplement(int value) {
        return mask(~value);
    }

    private static int twosComplement(int value) {
        return mask(~value + 1);
    }

    private static int signExtend(int value) {
        return (value << 16) >> 16;
    }

    public static int not(int value) {
        return logicalResult(~value);
    }

    public static int negate(int value) {
        return arithmeticResult(twosComplement(value));
    }

    public static int abs(int value) {
        if (isNegative(value)) {
            return negate(value);
        } else {
            return arithmeticResult(value);
        }
    }

    public static int extend(int value) {
        if ((value & 0x0080) == 0) {
            return logicalResult(value & 0x00FF);
        } else {
            return logicalResult(value | 0xFF00);
        }
    }

    public static int add(int left, int right) {
        return arithmeticResult(left + right);
    }

    public static int subtract(int left, int right) {
        return add(left, twosComplement(right));
    }

    public static int multiply(int left, int right) {
        return arithmeticResult(signExtend(left) * signExtend(right));
    }

    public static int divide(int left, int right) {
        if (right == 0) return divisionByZero();
        return arithmeticResult(signExtend(left) / signExtend(right));
    }

    public static int modulo(int left, int right) {
        if (right == 0) return divisionByZero();
        return arithmeticResult(signExtend(left) % signExtend(right));
    }

    public static int shiftArithmeticLeft(int left, int right) {
        return arithmeticResult(signExtend(left) << signExtend(right));
    }

    public static int shiftArithmeticRight(int left, int right) {
        return arithmeticResult(signExtend(left) >> signExtend(right));
    }

    public static int shiftLogicalRight(int left, int right) {
        return arithmeticResult(left >>> right);
    }

    public static int and(int left, int right) {
        return logicalResult(left & right);
    }

    public static int or(int left, int right) {
        return logicalResult(left | right);
    }

    public static int xor(int left, int right) {
        return logicalResult(left ^ right);
    }
}
