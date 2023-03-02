public enum Opcode {

    // mnemonic(opcode, mask, bytes, cycles)

    LBX  (0x1000, 0xF000, 2, 3),  // LBX   R,Rx,disp4    Load Byte Indexed
    LWX  (0x2000, 0xF000, 2, 4),  // LWX   R,Rx,disp4    Load Word Indexed
    STBX (0x3000, 0xF000, 2, 3),  // STBX  R,Rx,disp4    Store Byte Indexed
    STWX (0x4000, 0xF000, 2, 4),  // STWX  R,Rx,disp4    Store Word Indexed

    ADD  (0x5000, 0xF000, 2, 2),  // ADD   R1,R2,R       Add
    SUB  (0x6000, 0xF000, 2, 2),  // SUB   R1,R2,R       Subtract
    MUL  (0x7000, 0xF000, 2, 3),  // MUL   R1,R2,R       Multiply
    DIV  (0x8000, 0xF000, 2, 4),  // DIV   R1,R2,R       Divide
    MOD  (0x9000, 0xF000, 2, 4),  // MOD   R1,R2,R       Modulo
    AND  (0xA000, 0xF000, 2, 2),  // AND   R1,R2,R       Logical And
    OR   (0xB000, 0xF000, 2, 2),  // OR    R1,R2,R       Logical Or
    XOR  (0xC000, 0xF000, 2, 2),  // XOR   R1,R2,R       Logical Xor
    SAL  (0xD000, 0xF000, 2, 2),  // SAL   R1,R2,R       Shift Arithmetic Left
    SAR  (0xE000, 0xF000, 2, 2),  // SAR   R1,R2,R       Shift Arithmetic Right
    SLR  (0xF000, 0xF000, 2, 2),  // SLR   R1,R2,R       Shift Logical Right

    MOV  (0x0100, 0xFF00, 2, 2),  // MOV   Rs,R          Move Register
    CMP  (0x0200, 0xFF00, 2, 2),  // CMP   R1,R2         Compare Register
    LDQ  (0x0300, 0xFF00, 2, 2),  // LDQ   R,val4        Load Quick
    CMPQ (0x0400, 0xFF00, 2, 2),  // CMPQ  R,val4        Compare Quick
    ADDQ (0x0500, 0xFF00, 2, 2),  // ADDQ  R,val4        Add Quick
    SUBQ (0x0600, 0xFF00, 2, 2),  // SUBQ  R,val4        Subtract Quick
    MULQ (0x0700, 0xFF00, 2, 3),  // MULQ  R,val4        Multiply Quick
    DIVQ (0x0800, 0xFF00, 2, 4),  // DIVQ  R,val4        Divide Quick
    MODQ (0x0900, 0xFF00, 2, 4),  // MODQ  R,val4        Modulo Quick
    ANDQ (0x0A00, 0xFF00, 2, 2),  // ANDQ  R,val4        Logical And Quick
    ORQ  (0x0B00, 0xFF00, 2, 2),  // ORQ   R,val4        Logical Or Quick
    XORQ (0x0C00, 0xFF00, 2, 2),  // XORQ  R,val4        Logical Xor Quick
    SALQ (0x0D00, 0xFF00, 2, 2),  // SALQ  R,val4        Shift Arithmetic Left Quick
    SARQ (0x0E00, 0xFF00, 2, 2),  // SARQ  R,val4        Shift Arithmetic Right Quick
    SLRQ (0x0F00, 0xFF00, 2, 2),  // SLRQ  R,val4        Shift Logical Right Quick

    LI   (0x0010, 0xFFF0, 4, 4),  // LI    R,val16       Load Immediate
    LB   (0x0020, 0xFFF0, 4, 5),  // LB    R,addr        Load Byte
    LW   (0x0030, 0xFFF0, 4, 6),  // LW    R,addr        Load Word
    STB  (0x0040, 0xFFF0, 4, 5),  // STB   R,addr        Store Byte
    STW  (0x0050, 0xFFF0, 4, 6),  // STW   R,addr        Store Word

    NOT  (0x0060, 0xFFF0, 2, 2),  // NOT   R             Logical Not (one's complement)
    NEG  (0x0070, 0xFFF0, 2, 2),  // NEG   R             Negate (two's complement)
    ABS  (0x0080, 0xFFF0, 2, 2),  // ABS   R             Absolute Value
    EXT  (0x0090, 0xFFF0, 2, 2),  // EXT   R             Sign Extend
    PUSH (0x00A0, 0xFFF0, 2, 4),  // PUSH  R             Push Register
    POP  (0x00B0, 0xFFF0, 2, 4),  // POP   R             Pop Register
    IN   (0x00C0, 0xFFF0, 2, 3),  // IN    R             Input
    OUT  (0x00D0, 0xFFF0, 2, 3),  // OUT   R             Output
    JUMP (0x00E0, 0xFFF0, 2, 2),  // JUMP  R             Jump (indirect)
    EXEC (0x00F0, 0xFFF0, 2, 4),  // EXEC  R             Execute (indirect call)

    B    (0x0001, 0xFFFF, 4, 4),  // B     addr          Branch (always)
    BEQ  (0x0002, 0xFFFF, 4, 4),  // BEQ   addr          Branch if equal
    BNE  (0x0003, 0xFFFF, 4, 4),  // BNE   addr          Branch if not equal
    BLT  (0x0004, 0xFFFF, 4, 4),  // BLT   addr          Branch if <  (signed)
    BLE  (0x0005, 0xFFFF, 4, 4),  // BLE   addr          Branch if <= (signed)
    BGE  (0x0006, 0xFFFF, 4, 4),  // BGE   addr          Branch if >= (signed)
    BGT  (0x0007, 0xFFFF, 4, 4),  // BGT   addr          Branch if >  (signed)
    BLTU (0x0008, 0xFFFF, 4, 4),  // BLTU  addr          Branch if <  (unsigned) (BC)
    BLEU (0x0009, 0xFFFF, 4, 4),  // BLEU  addr          Branch if <= (unsigned)
    BGEU (0x000A, 0xFFFF, 4, 4),  // BGEU  addr          Branch if >= (unsigned) (BNC)
    BGTU (0x000B, 0xFFFF, 4, 4),  // BGTU  addr          Branch if >  (unsigned)
    CALL (0x000C, 0xFFFF, 4, 6),  // CALL  addr          Call (direct)
    RET  (0x000D, 0xFFFF, 2, 4),  // RET                 Return
    REGS (0x000E, 0xFFFF, 2, 2),  // REGS                Display the contents of the registers
    DUMP (0x000F, 0xFFFF, 2, 2),  // DUMP                Dump the contents of memory
    HALT (0x0000, 0xFFFF, 2, 2),  // HALT                Halt

    //  Pseudo operatons:         // Psuedo OP:     Same as:       New Description;

    NOP  (0x0100, 0xFF00, 2, 2),  // NOP            MOV   R0,R0    No operation
    INC  (0x0510, 0xFF00, 2, 2),  // INC   R        ADDQ  R,1      Increment register
    DEC  (0x0610, 0xFF00, 2, 2),  // DEC   R        SUBQ  R,1      Decrement register
    CLR  (0x0300, 0xFF00, 2, 2),  // CLR   R        LDQ   R,0      Clear register
    TEST (0x0400, 0xFF00, 2, 2),  // TEST  R        CMPQ  R,0      Test register
    SLL  (0xD000, 0xF000, 2, 2),  // SLL   R        SAL   R1,R2,R  Shift Logial Left
    SLLQ (0x0D00, 0xFF00, 2, 2),  // SLLQ  R,val    SALQ  R,val4   Shift Logical Left Quick
    BE   (0x0002, 0xFFFF, 4, 4),  // BE    addr     BEQ   addr     Branch if equal
    BZ   (0x0002, 0xFFFF, 4, 4),  // BZ    addr     BEQ   addr     Branch if zero
    BNZ  (0x0003, 0xFFFF, 4, 4),  // BNZ   addr     BNE   addr     Branch if non zero
    BC   (0x0008, 0xFFFF, 4, 4),  // BC    addr     BLTU  addr     Branch if carry 
    BNC  (0x000A, 0xFFFF, 4, 4),  // BNC   addr     BGEU  addr     Branch if no carry
    BL   (0x0004, 0xFFFF, 4, 4),  // BL    addr     BLT   addr     Branch if less (signed)
    BG   (0x0007, 0xFFFF, 4, 4),  // BG    addr     BGT   addr     Branch if greater (signed)
    BLU  (0x0008, 0xFFFF, 4, 4),  // BLU   addr     BLTU  addr     Branch if less (unsigned)
    BGU  (0x000B, 0xFFFF, 4, 4);  // BGU   addr     BGTU  addr :   Branch if greater (unsigned)

    private final int opcode;
    private final int mask;
    private final int size;
    private final int cycles;

    private Opcode(int opcode, int mask, int size, int cycles) {
        this.opcode = opcode;
        this.mask   = mask;
        this.size   = size;
        this.cycles = cycles;
    }

    public int opcode() { return this.opcode; }
    public int mask()   { return this.mask; }
    public int size()   { return this.size; }
    public int cycles() { return this.cycles; }

    @Override
    public String toString() {
        return this.name();
    }

    public static Opcode decode(int instruction) {
        switch (instruction & 0xF000) {
            case 0x0000: break;
            case 0x1000: return Opcode.LBX;
            case 0x2000: return Opcode.LWX; 
            case 0x3000: return Opcode.STBX;
            case 0x4000: return Opcode.STWX;
            case 0x5000: return Opcode.ADD;
            case 0x6000: return Opcode.SUB;
            case 0x7000: return Opcode.MUL;
            case 0x8000: return Opcode.DIV;
            case 0x9000: return Opcode.MOD;
            case 0xA000: return Opcode.AND;
            case 0xB000: return Opcode.OR;
            case 0xC000: return Opcode.XOR;
            case 0xD000: return Opcode.SAL;
            case 0xE000: return Opcode.SAR;
            case 0xF000: return Opcode.SLR;
        }

        switch (instruction & 0x0F00) {
            case 0x0000: break;
            case 0x0100: return Opcode.MOV;
            case 0x0200: return Opcode.CMP;
            case 0x0300: return Opcode.LDQ;
            case 0x0400: return Opcode.CMPQ;
            case 0x0500: return Opcode.ADDQ;
            case 0x0600: return Opcode.SUBQ;
            case 0x0700: return Opcode.MULQ;
            case 0x0800: return Opcode.DIVQ;
            case 0x0900: return Opcode.MODQ;
            case 0x0A00: return Opcode.ANDQ;
            case 0x0B00: return Opcode.ORQ;
            case 0x0C00: return Opcode.XORQ;
            case 0x0D00: return Opcode.SALQ;
            case 0x0E00: return Opcode.SARQ;
            case 0x0F00: return Opcode.SLRQ;
        }

        switch (instruction & 0x00F0) {
            case 0x0000: break;
            case 0x0010: return Opcode.LI;
            case 0x0020: return Opcode.LB;
            case 0x0030: return Opcode.LW;
            case 0x0040: return Opcode.STB;
            case 0x0050: return Opcode.STW;
            case 0x0060: return Opcode.NOT;
            case 0x0070: return Opcode.NEG;
            case 0x0080: return Opcode.ABS;
            case 0x0090: return Opcode.EXT;
            case 0x00A0: return Opcode.PUSH;
            case 0x00B0: return Opcode.POP;
            case 0x00C0: return Opcode.IN;
            case 0x00D0: return Opcode.OUT;
            case 0x00E0: return Opcode.JUMP;
            case 0x00F0: return Opcode.EXEC;
        }
    
        switch (instruction & 0x000F) {
            case 0x0000: return Opcode.HALT;
            case 0x0001: return Opcode.B;
            case 0x0002: return Opcode.BEQ;
            case 0x0003: return Opcode.BNE;
            case 0x0004: return Opcode.BLT;
            case 0x0005: return Opcode.BLE;
            case 0x0006: return Opcode.BGE;
            case 0x0007: return Opcode.BGT;
            case 0x0008: return Opcode.BLTU;
            case 0x0009: return Opcode.BLEU;
            case 0x000A: return Opcode.BGEU;
            case 0x000B: return Opcode.BGTU;
            case 0x000C: return Opcode.CALL;
            case 0x000D: return Opcode.RET;
            case 0x000E: return Opcode.REGS;
            case 0x000F: return Opcode.DUMP;
        }

        return null;
    }

    static {
        int errors = 0;
        for (int instruction = 0; instruction <= 0xFFFF; instruction++) {
            Opcode opcode = decode(instruction);
            if (opcode == null) {
                System.err.printf("Unable to decode %04x\n", instruction);
                errors++;
            } else if (opcode.opcode != (instruction & opcode.mask)) {
                System.err.printf("Decoding error for %s (%04x)\n", opcode.name(), instruction);
                errors++;
            }
        }
        if (errors > 0) {
            throw new RuntimeException("Opcode decoding tables are inconsistent");
        }
    }
}
