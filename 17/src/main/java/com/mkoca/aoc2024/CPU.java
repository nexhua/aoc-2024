package com.mkoca.aoc2024;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CPU {

    long[] registers; // A B C
    long ip; // instruction pointer
    List<Pair<Long>> instructions;
    boolean[] flags; // increaseIpAfterOp
    List<String> out;
    public boolean HALTED;

    public CPU(long A, long B, long C, List<Pair<Long>> instructions) {
        registers = new long[]{A, B, C};
        ip = 0L;
        this.instructions = instructions;
        flags = new boolean[1];
        out = new ArrayList<>();
        HALTED = false;
    }


    public void operate() {
        if (ip >= instructions.size()) {
            HALTED = true;
            return;
        }

        Pair<Long> pair = instructions.get((int) ip);
        switch (Math.toIntExact(pair.first)) {
            case 0: // ADV
                Long operand = getOperand(pair.second, AccessMode.COMBO);
                long res = (long) (registers[0] / Math.pow(2, operand));
                registers[0] = res;
                flags[0] = true;
                break;
            case 1: // BXL
                operand = getOperand(pair.second, AccessMode.LITERAL);
                res = registers[1] ^ operand;
                registers[1] = res;
                flags[0] = true;
                break;
            case 2: // BST
                operand = getOperand(pair.second, AccessMode.COMBO);
                registers[1] = operand % 8;
                flags[0] = true;
                break;
            case 3: // JNZ
                if (registers[0] != 0) {
                    operand = getOperand(pair.second, AccessMode.LITERAL);
                    ip = operand;
                    flags[0] = false;
                } else {
                    flags[0] = true;
                }
                break;
            case 4: // BXC
                res = registers[1] ^ registers[2];
                registers[1] = res;
                flags[0] = true;
                break;
            case 5: // OUT
                operand = getOperand(pair.second, AccessMode.COMBO);
                out.add(String.valueOf(operand % 8));
                break;
            case 6: // BDV
                operand = getOperand(pair.second, AccessMode.COMBO);
                res = (long) (registers[0] / Math.pow(2, operand));
                registers[1] = res;
                flags[0] = true;
                break;
            case 7: // CDV
                operand = getOperand(pair.second, AccessMode.COMBO);
                res = (long) (registers[0] / Math.pow(2, operand));
                registers[2] = res;
                flags[0] = true;
                break;
            default:
                System.out.println("ERROR");
                break;
        }

        if (flags[0]) ip++;
    }

    private Long getOperand(Long a, AccessMode accessMode) {
        if (accessMode == AccessMode.LITERAL) return a;

        if (a <= 3) {
            return a;
        } else {
            if (a >= 7) throw new IllegalArgumentException("Operand value can not be or equal than 7");

            return registers[(int) (a % 4)];
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Register A: %d\n", registers[0]));
        sb.append(String.format("Register B: %d\n", registers[1]));
        sb.append(String.format("Register C: %d\n", registers[2]));
        sb.append(String.format("Instructions: %s", instructions.stream().map(String::valueOf).collect(Collectors.joining(", "))));
        return sb.toString();
    }

    public String getOut() {
        return String.join(",", this.out);
    }

    enum AccessMode {
        LITERAL, COMBO
    }
}
