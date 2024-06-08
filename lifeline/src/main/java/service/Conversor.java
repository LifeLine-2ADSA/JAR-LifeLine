package service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat fmt = new DecimalFormat("0.00", symbols);
        String string = fmt.format(valorDouble);
        return Double.parseDouble(string);
    }

    public static Double converterDoubleTresDecimais(Double valorDouble) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat fmt = new DecimalFormat("0.000", symbols);
        String string = fmt.format(valorDouble);
        return Double.parseDouble(string);
    }
}
