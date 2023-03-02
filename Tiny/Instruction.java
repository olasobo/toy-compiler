public class Instruction {

    private static final int COUNT = Opcode.values().length;
    private static final Instruction[] instructions = new Instruction[COUNT];

    private final Opcode opcode;
    private final Operand[] operands;
    private final int words;


    private Instruction(Opcode opcode, Operand[] operands) {
        this.opcode = opcode;
        this.operands = operands;
        this.words = words(operands);
        instructions[opcode.ordinal()] = this;
    }

    public Opcode opcode() { return this.opcode; }
    public String name()   { return this.opcode.name(); }
    public int words()     { return this.words; }
    public int size()      { return this.opcode.size(); }
    public int cycles()    { return this.opcode.cycles(); }
    public int operands()  { return this.operands.length; }

    public Operand getOperand(int index) {
        return this.operands[index];
    }

    @Override
    public String toString() {
        return this.opcode.name();
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    private static int words(Operand[] operands) {
        int words = 1;
        for (Operand operand : operands) {
            words += operand.size();
        }
        return words;
    }


    public static Instruction valueOf(Opcode opcode) {
        if (opcode != null) {
            return instructions[opcode.ordinal()];
        } else {
            return null;
        }
    }

    public static Instruction valueOf(String mnemonic) {
        return Instruction.valueOf(Opcode.valueOf(mnemonic.toUpperCase()));
    }

    public static Instruction valueOf(int instruction) {
        return Instruction.valueOf(Opcode.decode(instruction));
    }

    private static final Operand R       = Operand.TARGET;
    private static final Operand Rs      = Operand.SOURCE;
    private static final Operand R1      = Operand.LEFT;
    private static final Operand R2      = Operand.RIGHT;
    private static final Operand Rx      = Operand.BASE;
    private static final Operand Disp4   = Operand.OFFSET;
    private static final Operand Value4  = Operand.QUICK;
    private static final Operand Value16 = Operand.VALUE;
    private static final Operand Address = Operand.ADDRESS;

    public static final Instruction LBX  = new Instruction(Opcode.LBX,  new Operand[] { R, Rx, Value4 });
    public static final Instruction LWX  = new Instruction(Opcode.LWX,  new Operand[] { R, Rx, Value4 });
    public static final Instruction STBX = new Instruction(Opcode.STBX, new Operand[] { R, Rx, Value4 });
    public static final Instruction STWX = new Instruction(Opcode.STWX, new Operand[] { R, Rx, Value4 });

    public static final Instruction ADD  = new Instruction(Opcode.ADD,  new Operand[] { R1, R2, R });
    public static final Instruction SUB  = new Instruction(Opcode.SUB,  new Operand[] { R1, R2, R });
    public static final Instruction MUL  = new Instruction(Opcode.MUL,  new Operand[] { R1, R2, R });
    public static final Instruction DIV  = new Instruction(Opcode.DIV,  new Operand[] { R1, R2, R });
    public static final Instruction MOD  = new Instruction(Opcode.MOD,  new Operand[] { R1, R2, R });
    public static final Instruction AND  = new Instruction(Opcode.AND,  new Operand[] { R1, R2, R });
    public static final Instruction OR   = new Instruction(Opcode.OR,   new Operand[] { R1, R2, R });
    public static final Instruction XOR  = new Instruction(Opcode.XOR,  new Operand[] { R1, R2, R });
    public static final Instruction SAL  = new Instruction(Opcode.SAL,  new Operand[] { R1, R2, R });
    public static final Instruction SAR  = new Instruction(Opcode.SAR,  new Operand[] { R1, R2, R });
    public static final Instruction SLR  = new Instruction(Opcode.SLR,  new Operand[] { R1, R2, R });

    public static final Instruction MOV  = new Instruction(Opcode.MOV,  new Operand[] { Rs, R });
    public static final Instruction CMP  = new Instruction(Opcode.CMP,  new Operand[] { Rs, R });

    public static final Instruction LDQ  = new Instruction(Opcode.LDQ,  new Operand[] { R, Value4 });
    public static final Instruction CMPQ = new Instruction(Opcode.CMPQ, new Operand[] { R, Value4 });
    public static final Instruction ADDQ = new Instruction(Opcode.ADDQ, new Operand[] { R, Value4 });
    public static final Instruction SUBQ = new Instruction(Opcode.SUBQ, new Operand[] { R, Value4 });
    public static final Instruction MULQ = new Instruction(Opcode.MULQ, new Operand[] { R, Value4 });
    public static final Instruction DIVQ = new Instruction(Opcode.DIVQ, new Operand[] { R, Value4 });
    public static final Instruction MODQ = new Instruction(Opcode.MODQ, new Operand[] { R, Value4 });
    public static final Instruction ANDQ = new Instruction(Opcode.ANDQ, new Operand[] { R, Value4 });
    public static final Instruction ORQ  = new Instruction(Opcode.ORQ,  new Operand[] { R, Value4 });
    public static final Instruction XORQ = new Instruction(Opcode.XORQ, new Operand[] { R, Value4 });
    public static final Instruction SALQ = new Instruction(Opcode.SALQ, new Operand[] { R, Value4 });
    public static final Instruction SARQ = new Instruction(Opcode.SARQ, new Operand[] { R, Value4 });
    public static final Instruction SLRQ = new Instruction(Opcode.SLRQ, new Operand[] { R, Value4 });

    public static final Instruction LI   = new Instruction(Opcode.LI,   new Operand[] { R, Value16 });
    public static final Instruction LB   = new Instruction(Opcode.LB,   new Operand[] { R, Address });
    public static final Instruction LW   = new Instruction(Opcode.LW,   new Operand[] { R, Address });
    public static final Instruction STB  = new Instruction(Opcode.STB,  new Operand[] { R, Address });
    public static final Instruction STW  = new Instruction(Opcode.STW,  new Operand[] { R, Address });

    public static final Instruction NOT  = new Instruction(Opcode.NOT,  new Operand[] { R });
    public static final Instruction NEG  = new Instruction(Opcode.NEG,  new Operand[] { R });
    public static final Instruction ABS  = new Instruction(Opcode.ABS,  new Operand[] { R });
    public static final Instruction EXT  = new Instruction(Opcode.EXT,  new Operand[] { R });
    public static final Instruction PUSH = new Instruction(Opcode.PUSH, new Operand[] { R });
    public static final Instruction POP  = new Instruction(Opcode.POP,  new Operand[] { R });
    public static final Instruction IN   = new Instruction(Opcode.IN,   new Operand[] { R });
    public static final Instruction OUT  = new Instruction(Opcode.OUT,  new Operand[] { R });
    public static final Instruction JUMP = new Instruction(Opcode.JUMP, new Operand[] { R });
    public static final Instruction EXEC = new Instruction(Opcode.EXEC, new Operand[] { R });

    public static final Instruction B    = new Instruction(Opcode.B,    new Operand[] { Address });
    public static final Instruction BEQ  = new Instruction(Opcode.BEQ,  new Operand[] { Address });
    public static final Instruction BNE  = new Instruction(Opcode.BNE,  new Operand[] { Address });
    public static final Instruction BLT  = new Instruction(Opcode.BLT,  new Operand[] { Address });
    public static final Instruction BLE  = new Instruction(Opcode.BLE,  new Operand[] { Address });
    public static final Instruction BGE  = new Instruction(Opcode.BGE,  new Operand[] { Address });
    public static final Instruction BGT  = new Instruction(Opcode.BGT,  new Operand[] { Address });
    public static final Instruction BLTU = new Instruction(Opcode.BLTU, new Operand[] { Address });
    public static final Instruction BLEU = new Instruction(Opcode.BLEU, new Operand[] { Address });
    public static final Instruction BGEU = new Instruction(Opcode.BGEU, new Operand[] { Address });
    public static final Instruction BGTU = new Instruction(Opcode.BGTU, new Operand[] { Address });
    public static final Instruction CALL = new Instruction(Opcode.CALL, new Operand[] { Address });
    public static final Instruction RET  = new Instruction(Opcode.RET,  new Operand[] {});
    public static final Instruction REGS = new Instruction(Opcode.REGS, new Operand[] {});
    public static final Instruction DUMP = new Instruction(Opcode.DUMP, new Operand[] {});
    public static final Instruction HALT = new Instruction(Opcode.HALT, new Operand[] {});

    // Pseudo operations

    public static final Instruction NOP  = new Instruction(Opcode.NOP,  new Operand[] {});
    public static final Instruction INC  = new Instruction(Opcode.INC,  new Operand[] { R });
    public static final Instruction DEC  = new Instruction(Opcode.DEC,  new Operand[] { R });
    public static final Instruction CLR  = new Instruction(Opcode.CLR,  new Operand[] { R });
    public static final Instruction TEST = new Instruction(Opcode.TEST, new Operand[] { R });
    public static final Instruction SLL  = new Instruction(Opcode.SAL,  new Operand[] { R1, R2, R });
    public static final Instruction SLLQ = new Instruction(Opcode.SALQ, new Operand[] { R, Value4 });
    public static final Instruction BE   = new Instruction(Opcode.BE,   new Operand[] { Address });
    public static final Instruction BZ   = new Instruction(Opcode.BZ,   new Operand[] { Address });
    public static final Instruction BNZ  = new Instruction(Opcode.BNZ,  new Operand[] { Address });
    public static final Instruction BC   = new Instruction(Opcode.BC,   new Operand[] { Address });
    public static final Instruction BNC  = new Instruction(Opcode.BNC,  new Operand[] { Address });
    public static final Instruction BL   = new Instruction(Opcode.BL,   new Operand[] { Address });
    public static final Instruction BG   = new Instruction(Opcode.BG,   new Operand[] { Address });
    public static final Instruction BLU  = new Instruction(Opcode.BLU,  new Operand[] { Address });
    public static final Instruction BGU  = new Instruction(Opcode.BGU,  new Operand[] { Address });  
}
