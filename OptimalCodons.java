/**
 * Enum to store the optimal codon for each amino acid.
 */
public enum OptimalCodons {
    A("GCT"),
    R("CGT"),
    N("AAC"),
    D("GAC"),
    C("TGC"),
    Q("CAG"),
    E("GAG"),
    G("GGC"),
    H("CAC"),
    I("ATT"),
    K("AAG"),
    L("CTT"),
    M("ATG"),
    F("TTC"),
    P("CCA"),
    S("TCT"),
    T("ACT"),
    Y("TAC"),
    V("GTG"),
    W("TGG"),
    stop("TAG");

    private final String i;
    // Function to convert from OptimalCodon type to a string.
    public String toString(){
        return i;
    }
    // OptimalCodon class constructor.
    OptimalCodons(String i) {
        this.i = i;
    }
}
