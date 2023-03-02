import java.io.IOException;

public class Execute {

    public static boolean trace = false;    // Display each instruction executed
    public static boolean debug = false;    // Enable REGS and DUMP instructions
    public static boolean silent = false;   // Silence output from OUT instructions
    private static int cycles = 0;

    private static int fetch() {
        return Memory.readWord(Register.PC.postIncrement(2));
    }

    private static void trace(int address) {
        if (Execute.trace) {
            Disassembler.disassemble(address);
        }
    }

    public static void execute() {
        trace(Register.PC.get());
        int instruction = fetch();
        Opcode opcode = Opcode.decode(instruction);
        Execute.cycles += opcode.cycles();

        switch (opcode) {
            case LB: {  // Load Byte
                Register register = Operand.TARGET.getRegister(instruction);
                register.set(Memory.readByte(fetch()));
                break;
            }

            case LW: {  // Load Word
                Register register = Operand.TARGET.getRegister(instruction);
                register.set(Memory.readWord(fetch()));
                break;
            }

            case STB: {  // Store Byte
                Register register = Operand.TARGET.getRegister(instruction);
                Memory.writeByte(fetch(), register.get());
                break;
            }

            case STW: {  // Store Word
                Register register = Operand.TARGET.getRegister(instruction);
                Memory.writeWord(fetch(), register.get());
                break;
            }

            case LBX: {  // Load Byte Indexed
                Register target = Operand.TARGET.getRegister(instruction);
                Register base = Operand.BASE.getRegister(instruction);
                int offset = Operand.OFFSET.getValue(instruction);
                target.set(Memory.readByte(base.get() + offset));
                break;
            }

            case LWX: {  // Load Word Indexed
                Register target = Operand.TARGET.getRegister(instruction);
                Register base = Operand.BASE.getRegister(instruction);
                int offset = Operand.OFFSET.getValue(instruction);
                target.set(Memory.readWord(base.get() + offset));
                break;
            }

            case STBX: {  // Store Byte Indexed
                Register source = Operand.TARGET.getRegister(instruction);
                Register base = Operand.BASE.getRegister(instruction);
                int offset = Operand.OFFSET.getValue(instruction);
                Memory.writeByte(base.get() + offset, source.get());
                break;
            }

            case STWX: {  // Store Word Indexed
                Register source = Operand.TARGET.getRegister(instruction);
                Register base = Operand.BASE.getRegister(instruction);
                int offset = Operand.OFFSET.getValue(instruction);
                Memory.writeWord(base.get() + offset, source.get());
                break;
            }

            case PUSH: {  // Push Register
                Register register = Operand.TARGET.getRegister(instruction);
                Memory.push(register.get());
                break;
            }

            case POP: {  // Pop Register
                Register register = Operand.TARGET.getRegister(instruction);
                register.set(Memory.pop());
                break;
            }

            case IN: {  // Input
                Register register = Operand.TARGET.getRegister(instruction);
                try {
                    register.set(System.in.read());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                break;
            }

            case OUT: { // Output
                Register register = Operand.TARGET.getRegister(instruction);
                char c = (char) register.get();
                if (!Execute.silent) {
                    System.out.print(c);
                }
                break;
            }

            case MOV: {  // Move Register
                Register target = Operand.TARGET.getRegister(instruction);
                Register source = Operand.SOURCE.getRegister(instruction);
                target.set(source.get());
                break;
            }

            case LDQ: {  // Load Quick
                Register register = Operand.TARGET.getRegister(instruction);
                int value = Operand.QUICK.getValue(instruction);
                register.set(value);
                break;
            }

            case LI: {  // Load Immediate
                Register register = Operand.TARGET.getRegister(instruction);
                register.set(fetch());
                break;
            }

            case CMP: {  // Compare Registers
                Register right = Operand.TARGET.getRegister(instruction);
                Register left = Operand.SOURCE.getRegister(instruction);
                int result = Machine.subtract(left.get(), right.get());
                break;
            }

            case CMPQ: {  // Compare Quick
                Register left = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.subtract(left.get(), right);
                break;
            }

            case NOT: {  // Not (one's complement)
                Register target = Operand.TARGET.getRegister(instruction);
                int result = Machine.not(target.get());
                target.set(result);
                break;
            }

            case NEG: {  // Negate (two's complement)
                Register target = Operand.TARGET.getRegister(instruction);
                int result = Machine.negate(target.get());
                target.set(result);
                break;
            }

            case ABS: {  // Absolute value
                Register target = Operand.TARGET.getRegister(instruction);
                int result = Machine.abs(target.get());
                target.set(result);
                break;
            }

            case EXT: {  // Sign Extend
                Register target = Operand.TARGET.getRegister(instruction);
                int result = Machine.extend(target.get());
                target.set(result);
                break;
            }

            case ADD: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.add(left.get(), right.get());
                target.set(result);
                break;
            }

            case ADDQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.add(target.get(), right);
                target.set(result);
                break;
            }

            case SUB: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.subtract(left.get(), right.get());
                target.set(result);
                break;
            }

            case SUBQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.subtract(target.get(), right);
                target.set(result);
                break;
            }

            case MUL: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.multiply(left.get(), right.get());
                target.set(result);
                break;
            }

            case MULQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.multiply(target.get(), right);
                target.set(result);
                break;
            }

            case DIV: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.divide(left.get(), right.get());
                target.set(result);
                break;
            }

            case DIVQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.divide(target.get(), right);
                target.set(result);
                break;
            }

            case MOD: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.modulo(left.get(), right.get());
                target.set(result);
                break;
            }

            case MODQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.modulo(target.get(), right);
                target.set(result);
                break;
            }

            case AND: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.and(left.get(), right.get());
                target.set(result);
                break;
            }

            case ANDQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.and(target.get(), right);
                target.set(result);
                break;
            }

            case OR: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.or(left.get(), right.get());
                target.set(result);
                break;
            }

            case ORQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.or(target.get(), right);
                target.set(result);
                break;
            }

            case XOR: {
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.xor(left.get(), right.get());
                target.set(result);
                break;
            }

            case XORQ: {
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.xor(target.get(), right);
                target.set(result);
                break;
            }

            case SAL: {  // Shift Arithmetic Left
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.shiftArithmeticLeft(left.get(), right.get());
                target.set(result);
                break;
            }

            case SALQ: {  // Shift Arithmetic Left Quick
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.shiftArithmeticLeft(target.get(), right);
                target.set(result);
                break;
            }

            case SAR: {  // Shift Arithmetic Right
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.shiftArithmeticRight(left.get(), right.get());
                target.set(result);
                break;
            }

            case SARQ: {  // Shift Arithmetic Right Quick
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.shiftArithmeticRight(target.get(), right);
                target.set(result);
                break;
            }

            case SLR: {  // Shift Logical Right
                Register target = Operand.TARGET.getRegister(instruction);
                Register left = Operand.LEFT.getRegister(instruction);
                Register right = Operand.RIGHT.getRegister(instruction);
                int result = Machine.shiftLogicalRight(left.get(), right.get());
                target.set(result);
                break;
            }

            case SLRQ: {  // Shift Logical Right Quick
                Register target = Operand.TARGET.getRegister(instruction);
                int right = Operand.QUICK.getValue(instruction);
                int result = Machine.shiftLogicalRight(target.get(), right);
                target.set(result);
                break;
            }

            case JUMP: {  // Jump (indirect)
                Register register = Operand.TARGET.getRegister(instruction);
                Register.PC.set(register.get());
                break;
            }

            case EXEC: {  // Call (indirect)
                Register register = Operand.TARGET.getRegister(instruction);
                Memory.push(Register.PC.get());
                Register.PC.set(register.get());
                break;
            }

            case CALL: {  // Call (direct)
                int address = fetch();
                Memory.push(Register.PC.get());
                Register.PC.set(address);
                break;
            }

            case RET: {  // Return
                Register.PC.set(Memory.pop());
                break;
            }

            case B: {  // Branch (always)
                int address = fetch();
                Register.PC.set(address);
                break;
            }

            case BEQ: {  // Branch if Equal
                int address = fetch();
                if (Register.Flags.isEqual()) {
                    Register.PC.set(address);
                }
                break;
            }

            case BNE: {  // Branch if Not Equal
                int address = fetch();
                if (Register.Flags.isNotEqual()) {
                    Register.PC.set(address);
                }
                break;
            }

            case BLT: {  // Branch if Less
                int address = fetch();
                if (Register.Flags.isLess(true)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BLE: {  // Branch if Less or Equal
                int address = fetch();
                if (Register.Flags.isLessEqual(true)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BGE: {  // Branch if Greater or Equal
                int address = fetch();
                if (Register.Flags.isGreaterEqual(true)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BGT: {  // Branch if Greater
                int address = fetch();
                if (Register.Flags.isGreater(true)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BLTU: {  // Branch if Less (unsigned)
                int address = fetch();
                if (Register.Flags.isLess(false)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BLEU: {  // Branch if Less or Equal (unsigned)
                int address = fetch();
                if (Register.Flags.isLessEqual(false)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BGEU: {  // Branch if Greater or Equal (unsigned)
                int address = fetch();
                if (Register.Flags.isGreaterEqual(false)) {
                    Register.PC.set(address);
                }
                break;
            }

            case BGTU: {  // Branch if Greater (unsigned)
                int address = fetch();
                if (Register.Flags.isGreater(false)) {
                    Register.PC.set(address);
                }
                break;
            }

            case REGS:
                if (debug) {
                    Register.dump();
                }
                break;

            case DUMP:
                if (debug) {
                    Memory.save();
                }
                break;

            case HALT:
                Register.Flags.halted = true;
                break;

            default:
                throw new RuntimeException("Unimplemented Opcode");
        }
    }

    public static int run(int start) {
        Register.PC.set(start);
        Memory.reads = 0;
        Memory.writes = 0;
        do {
            execute();
        } while (!Register.Flags.halted);
        return Execute.cycles;
    }

    public static int run() {
        return run(0);
    }
}
