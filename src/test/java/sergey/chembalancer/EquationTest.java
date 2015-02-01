package sergey.chembalancer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sergey.chembalancer.chemistry.EquationBalancer;

public class EquationTest
{
	
	@Test
	public void q1() {
		String str = new EquationBalancer("Pb(N3)2 + Cr(MnO4)2 = Pb3O4 + NO + Cr2O3 + MnO2").getResult(false);
		assertEquals("15Pb(N3)2 + 44Cr(MnO4)2 -> 5Pb3O4 + 90NO + 22Cr2O3 + 88MnO2 ", str);
	}
	@Test
	public void q2() {
		String str = new EquationBalancer("P2I4 + P4 + H2O = PH4I + H3PO4").getResult(false);
		assertEquals("10P2I4 + 13P4 + 128H2O -> 40PH4I + 32H3PO4 ", str);
	}
	@Test
	public void q3() {
		String str = new EquationBalancer("C6H12O6 + KMnO4 + H2SO4 = CO2 + K2SO4 + MnSO4 + H2O").getResult(false);
		assertEquals("5C6H12O6 + 24KMnO4 + 36H2SO4 -> 30CO2 + 12K2SO4 + 24MnSO4 + 66H2O ", str);
	}
	@Test
	public void q4() {
		String str = new EquationBalancer("K4Fe(CN)6 + H2SO4 + KMnO4 = MnSO4 + Fe2(SO4)3 + K2SO4 + HNO3 + CO2 + H2O").getResult(false);
		assertEquals("10K4Fe(CN)6 + 218H2SO4 + 122KMnO4 -> 122MnSO4 + 5Fe2(SO4)3 + 81K2SO4 + 60HNO3 + 60CO2 + 188H2O ", str);
	}
	@Test
	public void q5() {
		String str = new EquationBalancer("H2 + Ca(CN)2 + NaAlF4 + FeSO4 + MgSiO3 + KI + H3PO4 + PbCrO4 + BrCl + CF2Cl2 + SO2 = PbBr2 + CrCl3 + MgCO3 + KAl(OH)4 + Fe(SCN)3 + PI3 + Na2SiO3 + CaF2 + H2O").getResult(false);
		assertEquals("88H2 + 15Ca(CN)2 + 6NaAlF4 + 10FeSO4 + 3MgSiO3 + 6KI + 2H3PO4 + 6PbCrO4 + 12BrCl + 3CF2Cl2 + 20SO2 -> 6PbBr2 + 6CrCl3 + 3MgCO3 + 6KAl(OH)4 + 10Fe(SCN)3 + 2PI3 + 3Na2SiO3 + 15CaF2 + 79H2O ", str);
	}
	@Test
	public void q6() {
		String str = new EquationBalancer("[Cr(N2H4CO)6]4[Cr(CN)6]3 + KMnO4 + H2SO4 = K2Cr2O7 + MnSO4 + CO2 + KNO3 + K2SO4 + H2O").getResult(false);
		assertEquals("10(Cr(N2H4CO)6)4(Cr(CN)6)3 + 1176KMnO4 + 1399H2SO4 -> 35K2Cr2O7 + 1176MnSO4 + 420CO2 + 660KNO3 + 223K2SO4 + 1879H2O ", str);
	}
}
