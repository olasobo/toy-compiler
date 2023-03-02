import java.io.PrintStream;

public class Disassembler {

	private static boolean isPrintable(char c) {
		return c > 32 && c < 127;
	}

	public static String asciiByte(int value) {
		char c = (char) (value & 0xFF);
		if (isPrintable(c)) {
			return Character.toString(c);
		} else {
			return ".";
		}
	}

	public static String decimalByte(int value) {
		return String.format("%3d", value & 0xFF);
	}

	public static String decimalWord(int value) {
		return String.format("%5d", value & 0xFFFF);
	}

	public static String hexByte(int value, boolean prefix) {
        String result = String.format("%02x", value & 0xFF).toUpperCase();
		return prefix ? "0x" + result : result;
	}

	public static String hexWord(int value, boolean prefix) {
        String result = String.format("%04x", value & 0xFFFF).toUpperCase();
		return prefix ? "0x" + result : result;
	}

	public static String hexByte(int value) {
		return hexByte(value, false);
	}

	public static String hexWord(int value) {
		return hexWord(value, false);
	}

    public static String formatAddress(int address) {
        return hexWord(address, true);
    }

	public static String formatChars(int[] bytes) {
		String result = "";
		for (int value : bytes) {
			result += asciiByte(value);
		}
		return result;
	}

    public static String formatBytes(int[] bytes, boolean decimal) {
        String separator = "";
        String result = "";
        int count = 0;
        for (int value : bytes) {
			result += separator;
			if (decimal) {
				result += decimalByte(value);
			} else {
				result += hexByte(value);
			}
            if (++count % 2 == 0) {
                separator = "  ";
            } else {
                separator = " ";
            }
        }
        return result;
    }

    public static String formatWords(int[] words, boolean decimal) {
        String separator = "";
        String result = "";
        int count = 0;
        for (int value : words) {
			result += separator;
			if (decimal) {
				result += decimalWord(value);
			} else {
				result += hexWord(value);
			}
			separator = " ";
        }
        return result;
    }

	public static String formatChars(int address, int count) {
		return formatChars(readBytes(address, count));
	}

    public static String formatBytes(int address, int count, boolean decimal) {
        return formatBytes(readBytes(address, count), decimal);
    }

	public static String formatBytes(int address, int count) {
		return formatBytes(address, count, false);
	}

    public static String formatWords(int address, int count, boolean decimal) {
        return formatBytes(readWords(address, count), decimal);
    }

	public static String formatWords(int address, int count) {
		return formatWords(readWords(address, count), false);
	}

    public static int[] readBytes(int address, int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = Memory.readByte(address+i);
        }
        return result;
    }
        
    public static int[] readWords(int address, int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = Memory.readWord(address+2*i);
        }
        return result;
    }

    public static String formatOperands(Instruction instruction, int address) {
        String result = "";
        String separator = "";
        int count = instruction.words();
        int[] words = readWords(address, count);

        for (int i = 0; i < instruction.operands(); i++) {
            Operand operand = instruction.getOperand(i);
            if (!operand.additionalWord()) {
                int value = operand.decode(words[0]);
                result += separator + operand.toString(value);
                separator = ",";
            }
        }

        int index = 1;
        for (int i = 0; i < instruction.operands(); i++) {
            Operand operand = instruction.getOperand(i);
            if (operand.additionalWord()) {
                result += separator + operand.toString(words[index++]);
                separator = ",";
            }
        }
        return result;
    }

    public static String formatOperands(int address) {
        Opcode opcode = Opcode.decode(Memory.readWord(address));
        Instruction instruction = Instruction.valueOf(opcode);
        return formatOperands(instruction, address);
    }

    private static String formatOpcode(Instruction instruction) {
        return String.format("%-6s", instruction.name());
    }

    public static String formatInstruction(Instruction instruction, int address){
        return formatOpcode(instruction) + formatOperands(instruction, address);
    }

    public static String formatInstruction(int address) {
        Opcode opcode = Opcode.decode(Memory.readWord(address));
        Instruction instruction = Instruction.valueOf(opcode);
        return formatInstruction(instruction, address);
    }

    public static String formatInstruction(
        Instruction instruction,
        int         address,
        boolean     includeAddress,
        boolean     includeCode,
        boolean     includeInstruction
    ) {
        String result = "";
        String separator = "  ";
        if (includeAddress) {
            result += formatAddress(address);
            separator = ": ";
        }
        if (includeCode) {
            result += separator;
            result += formatBytes(address, 2);
            separator = "  ";
        }
        if (includeInstruction) {
            result += separator;
            result += formatInstruction(instruction, address);
        }
        return result;
    }

    public static String formatInstruction(
        int         address,
        boolean     includeAddress,
        boolean     includeCode,
        boolean     includeInstruction
    ) {
        Instruction instruction = Instruction.valueOf(Memory.readWord(address));
        return formatInstruction(
            instruction,
            address,
            includeAddress,
            includeCode,
            includeInstruction
        );
    }


    public static void disassemble(
        PrintStream output,
        int         start,
        int         end,
        boolean     displayAddress,
        boolean     displayCode,
        boolean     displayInstruction
    ) {
        int address = start;
        while (address <= end) {
            Instruction instruction = Instruction.valueOf(Memory.readWord(address));
            output.println(
                formatInstruction(
                    instruction,
                    address,
                    displayAddress,
                    displayCode,
                    displayInstruction
                )
            );

            address += 2;
            for (int i = 1; i < instruction.words(); i++) {
                if(displayAddress) {
                    output.print(formatAddress(address) + ": ");
                }
                if (displayCode) {
                    output.print(formatBytes(address, 2) + "  ");
                }
                if (displayAddress | displayCode) output.println();
                address += 2;
            }
        }
    }

    public static void disassemble(
        PrintStream output,
        int         address,
        boolean     displayAddress,
        boolean     displayCode,
        boolean     displayInstruction
    ) {
        disassemble(
            output,
            address,
            address,
            displayAddress,
            displayCode,
            displayInstruction
        );
    }

    public static void disassemble(PrintStream output, int start, int end) {
        disassemble(output, start, end, true, true, true);
    }

    public static void disassemble(PrintStream output, int address) {
        disassemble(output, address, address, true, true, true);
    }

    public static void disassemble(int start, int end) {
        disassemble(System.out, start, end);
    }

    public static void disassemble(int address) {
        disassemble(address, address);
    }
}
