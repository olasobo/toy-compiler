public abstract class Operand {

    protected Operand() {
    }

    public int encode(int value) {
        throw new UnsupportedOperationException();
    }

    public int decode(int instruction) {
        throw new UnsupportedOperationException();
    }

    public abstract boolean isValid(int value);

    public boolean additionalWord() {
        return false;
    }

    public int size() {
        return additionalWord() ? 1 : 0;
    }

    public String toString(int value) {
        return Integer.toString(value);
    }

    // -- Register Operands -----------------------------------------------------------

    public static abstract class RegisterOperand extends Operand {

        protected RegisterOperand() {
            super();
        }

        public Register getRegister(int instruction) {
            return Register.valueOf(this.decode(instruction));
        }

        public int encode(Register register) {
            return this.encode(register.number());
        }

        @Override
        public boolean isValid(int value) {
            return value >= 0 && value < Register.REGISTERS;
        }

        @Override
        public String toString(int value) {
            return Register.valueOf(value).name();
        }
    }

    // -- Numeric Operands ------------------------------------------------------------

    public static abstract class NumericOperand extends Operand {
    
        private int lo;
        private int hi;
        private int bias;

        protected NumericOperand(int lo, int hi) {
            super();
            this.lo = lo;
            this.hi = hi;
            this.bias = hi - lo + 1;
        }

        protected int signed(int value) {
            return (value > this.hi) ? value - bias : value;
        }

        protected int unsigned(int value) {
            return (value < 0) ? value + bias : value;
        }

        @Override
        public boolean isValid(int value) {
            return value >= lo && value <= hi;
        }

        public int getValue(int instruction) {
            return this.signed(this.decode(instruction));
        }

        @Override
        public String toString(int value) {
            return Integer.toString(this.signed(value));
        }
    }

    // -- Operand Formats -------------------------------------------------------------

    public static final RegisterOperand TARGET = new RegisterOperand() {
        @Override
        public int decode(int instruction) {
            return getNibble4(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble4(value);
        }
    };

    public static final RegisterOperand SOURCE = new RegisterOperand() {
        @Override
        public int decode(int instruction) {
            return getNibble3(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble3(value);
        }
    };

    public static final RegisterOperand LEFT = new RegisterOperand() {
        @Override
        public int decode(int instruction) {
            return getNibble2(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble2(value);
        }
    };

    public static final RegisterOperand RIGHT = new RegisterOperand() {
        @Override
        public int decode(int instruction) {
            return getNibble3(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble3(value);
        }
    };

    public static final RegisterOperand BASE = new RegisterOperand() {
        @Override
        public int decode(int instruction) {
            return getNibble3(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble3(value);
        }
    };

    public static final NumericOperand OFFSET = new NumericOperand(-8, 7) {
        @Override
        public int decode(int instruction) {
            return getNibble2(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble2(value);
        }
    };

    public static final NumericOperand QUICK = new NumericOperand(0, 15) {
        @Override
        public int decode(int instruction) {
            return getNibble3(instruction);
        }

        @Override
        public int encode(int value) {
            return nibble3(value);
        }
    };

    public static final NumericOperand VALUE = new NumericOperand(-32768, 32767) {
        @Override
        public boolean additionalWord() {
            return true;
        }
    };


    public static final NumericOperand ADDRESS = new NumericOperand(0, 65536) {
        @Override
        public boolean additionalWord() {
            return true;
        }

        @Override
        public String toString(int value) {
            return "0x" + String.format("%04x", value).toUpperCase();
        }
    };

    // -- Utilities -------------------------------------------------------------------

    private static int getNibble1(int value) {
        return (value >> 12) & 0xF;
    }

    private static int getNibble2(int value) {
        return (value >> 8) & 0xF;
    }

    private static int getNibble3(int value) {
        return (value >> 4) & 0xF;
    }

    private static int getNibble4(int value) {
        return value & 0xF;
    }

    private static int nibble1(int value) {
        return (value & 0xF) << 12;
    }

    private static int nibble2(int value) {
        return (value & 0xF) << 8;
    }

    private static int nibble3(int value) {
        return (value & 0xF) << 4;
    }

    private static int nibble4(int value) {
        return value & 0xF;
    }
}
