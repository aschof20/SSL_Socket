/**
 * Enum to store the amino acids associated with different codons.
 */
public enum AminoAcids {
    AAA	("K"),
    AAC	("N"),
    AAG	("K"),
    AAT	("N"),
    ACA	("T"),
    ACC	("T"),
    ACG	("T"),
    ACT	("T"),
    AGA	("R"),
    AGC	("S"),
    AGG	("R"),
    AGT	("S"),
    ATA	("I"),
    ATC	("I"),
    ATG	("M"),
    ATT	("I"),
    CAA	("Q"),
    CAC	("H"),
    CAG	("Q"),
    CAT	("H"),
    CCA	("P"),
    CCC	("P"),
    CCG	("P"),
    CCT	("P"),
    CGA	("R"),
    CGC	("R"),
    CGG	("R"),
    CGT	("R"),
    CTA	("L"),
    CTC	("L"),
    CTG	("L"),
    CTT	("L"),
    GAA	("E"),
    GAC	("D"),
    GAG	("E"),
    GAT	("D"),
    GCA	("A"),
    GCC	("A"),
    GCG	("A"),
    GCT	("A"),
    GGA	("G"),
    GGC	("G"),
    GGG	("G"),
    GGT	("G"),
    GTA	("V"),
    GTC	("V"),
    GTG	("V"),
    GTT	("V"),
    TAA	("stop"),
    TAC	("Y"),
    TAG	("stop"),
    TAT	("Y"),
    TCA	("S"),
    TCC	("S"),
    TCG	("S"),
    TCT	("S"),
    TGA	("stop"),
    TGC	("C"),
    TGG	("W"),
    TGT	("C"),
    TTA	("L"),
    TTC	("F"),
    TTG	("L"),
    TTT	("F");

    private final String i;
    // Function to convert from AminoAcid type to a string.
    public String toString(){
        return i;
    }
    // AminoAcid class contructor.
    AminoAcids(String i) {
        this.i = i;
    }
}
