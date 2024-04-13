package maquina;

import java.text.DecimalFormat;

public class Conversor {
    private static final long KIBI = 1L << 10;
    private static final long MEBI = 1L << 20;
    private static final long GIBI = 1L << 30;
    private static final long TEBI = 1L << 40;
    private static final long PEBI = 1L << 50;
    private static final long EXBI = 1L << 60;

    public static Double formatarBytes(long bytes) {
        if (bytes < MEBI) { // KiB
            return formatarUnidades(bytes, KIBI);
        } else if (bytes < GIBI) { // MiB
            return formatarUnidades(bytes, MEBI);
        } else if (bytes < TEBI) { // GiB
            return formatarUnidades(bytes, GIBI);
        } else if (bytes < PEBI) { // TiB
            return formatarUnidades(bytes, TEBI);
        } else if (bytes < EXBI) { // PiB
            return formatarUnidades(bytes, PEBI);
        } else { // EiB
            return formatarUnidades(bytes, EXBI);
        }
    }

    private static Double formatarUnidades(long valor, long prefixo) {
        if (valor % prefixo == 0) {
            return (double) valor / prefixo;
        }
        return (double) valor / prefixo;
    }

    public static Double converterFrequencia(long frequencia) {
        return frequencia / 1000000000.0;
    }

    public static Double converterDoubleDoisDecimais(Double valorDouble) {
        DecimalFormat fmt = new DecimalFormat("0.00");
        String string = fmt.format(valorDouble);
        String[] part = string.split("[,]");
        String string2 = part[0]+"."+part[1];
        Double preco = Double.parseDouble(string2);
        return preco;
    }
}
