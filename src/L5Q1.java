import java.util.*;
import java.math.*;
public class L5Q1 {
	/* =======================================================================================================================
	 * LEVEL 5, FINAL QUESTION
	 * Oh no! You've managed to free the bunny prisoners and escape Commander Lambdas exploding space station, 
	 * but her team of elite starfighters has flanked your ship. If you dont jump to hyperspace, and fast, 
	 * youll be shot out of the sky!
	 * 
	 * Problem is, to avoid detection by galactic law enforcement, Commander Lambda planted her 
	 * space station in the middle of a quasar quantum flux field. In order to make the jump to 
	 * hyperspace, you need to know the configuration of celestial bodies in the quadrant you plan 
	 * to jump through. In order to do *that*, you need to figure out how many configurations each 
	 * quadrant could possibly have, so that you can pick the optimal quadrant through which youll 
	 * make your jump. 
	 * 
	 * There's something important to note about quasar quantum flux fields' configurations: when drawn 
	 * on a star grid, configurations are considered equivalent by grouping rather than by order. 
	 * That is, for a given set of configurations, if you exchange the position of any two columns 
	 * or any two rows some number of times, youll find that all of those configurations are equivalent 
	 * in that way - in grouping, rather than order.
	 * 
	 * Write a function answer(w, h, s) that takes 3 integers and returns the number of unique, 
	 * non-equivalent configurations that can be found on a star grid w blocks wide and h blocks 
	 * tall where each celestial body has s possible states. Equivalency is defined as above: any 
	 * two star grids with each celestial body in the same state where the actual order of the 
	 * rows and columns do not matter (and can thus be freely swapped around). Star grid 
	 * standardization means that the width and height of the grid will always be between 
	 * 1 and 12, inclusive. And while there are a variety of celestial bodies in each grid, 
	 * the number of states of those bodies is between 2 and 20, inclusive. The answer can be 
	 * over 20 digits long, so return it as a decimal string.  The intermediate values can also 
	 * be large, so you will likely need to use at least 64-bit integers.
	 * 
	 * For example, consider w=2, h=2, s=2. We have a 2x2 grid where each celestial body is either 
	 * in state 0 (for instance, silent) or state 1 (for instance, noisy).  We can examine which
	 * grids are equivalent by swapping rows and columns.
	 * 
	 * 00
	 * 00
	 * 
	 * In the above configuration, all celestial bodies are "silent" - that is, they have a state 
	 * of 0 - so any swap of row or column would keep it in the same state.
	 * 
	 * 00 00 01 10
	 * 01 10 00 00
	 * 
	 * 1 celestial body is emitting noise - that is, has a state of 1 - so swapping rows 
	 * and columns can put it in any of the 4 positions.  All four of the above 
	 * configurations are equivalent.
	 * 
	 * 00 11
	 * 11 00
	 * 
	 * 2 celestial bodies are emitting noise side-by-side.  Swapping columns leaves them 
	 * unchanged, and swapping rows simply moves them between the top and bottom.  
	 * In both, the *groupings* are the same: one row with two bodies in state 0, 
	 * one row with two bodies in state 1, and two columns with one of each state.
	 * 
	 * 01 10
	 * 01 10
	 * 
	 * 2 noisy celestial bodies adjacent vertically. This is symmetric to the side-by-side 
	 * case, but it is different because there's no way to transpose the grid.
	 * 
	 * 01 10
	 * 10 01
	 * 
	 * 2 noisy celestial bodies diagonally.  Both have 2 rows and 2 columns that 
	 * have one of each state, so they are equivalent to each other.
	 * 
	 * 01 10 11 11
	 * 11 11 01 10
	 * 
	 * 3 noisy celestial bodies, similar to the case where only one of four is noisy.
	 * 
	 * 11
	 * 11
	 * 
	 * 4 noisy celestial bodies.
	 * There are 7 distinct, non-equivalent grids in total, so answer(2, 2, 2) would return 7.
	 * 
	 * 
	 * Test cases
	 * ==========
	 * Inputs:
	 *     (int) w = 2
	 *     (int) h = 2
	 *     (int) s = 2
	 *     Output:
	 *     (string) "7"
	 *     
	 *     Inputs:
	 *     (int) w = 2
	 *     (int) h = 3
	 *     (int) s = 4
	 *     Output:
	 *     (string) "430"
	 * ======================================================================================================================= */

	/* Try applying Burnside's Lemma (https://en.wikipedia.org/wiki/Burnside's_lemma):
	 *
	 * No. of distinct grids = 1/|G| * sum(|X_g| for g in G)
	 *
	 * where |G| equals the total no. of elements that act on the matrix to permute it,
	 * and |X_g| is the no. of matrices that remain unchanged after each permutation.
	 *
	 * Now, since there are R rows and C columns, and there are R! ways to permute the row
	 * and C! ways to permute the column, the total # of ways to permute the row and/or col is:
	 * |G| = R!C!
	 *
	 * So the Burnside's lemma formula becomes:
	 *
	 * 1/R!C! * sum(|X_g| for g in G)
	 *
	 * Now how to compute |X^g|, and how to represent each element g that is in G?
	 * Probably best illustrated by solving an example case...
	 *
	 * Let's consider an example case of 2x3 matrix, and k = no. of states = 3.
	 *
	 * We'll represent the matrix with the following indices (don't worry about the # of states for the moment):
	 *
	 * 123
	 * 456
	 *
	 * This has R = 2 rows, and C = 3 cols, so the total ways to permute this matrix is:
	 * |G| = R!C! = 2!3! = 12.
	 *
	 * So let's iterate through each of the 12 possible ways to permute the matrix:
	 * 1) leave row alone, and leave col alone.
	 * 2) leave row alone, and swap columns 1 and 2.
	 * 3) leave row alone, and swap columns 1 and 3.
	 * 4) leave row alone, and swap columns 2 and 3.
	 * 5) leave row alone, and swap columns 2 and 3 followed by swapping columns 1 and 2.
	 * 6) leave row alone, and swap columns 1 and 2 followed by swapping columns 2 and 3.
	 * 7) swap rows, and leave col alone.
	 * 8) swap rows, and swap columns 1 and 2.
	 * 9) swap rows, and swap columns 1 and 3.
	 * 10)swap rows, and swap columns 2 and 3.
	 * 11)swap rows, and swap columns 2 and 3 followed by swapping columns 1 and 2.
	 * 12)swap rows, and swap columns 1 and 2 followed by swapping columns 2 and 3.
	 *
	 * So that's the 12 ways to permute the matrix. Next we need to figure out |X_g| for
	 * each of the above permutations, then add them up, then divide by R!C! as given by the lemma.
	 *
	 * 1) if we leave everything alone, then we simply have the following transformation:
	 * 123 -> 123
	 * 456    456
	 *
	 * Everything stays in its original place. So, given k = 3 colors, how many matrices exist
	 * that remain unchanged after the above "permutation" operation?
	 *
	 * Well, we have 3 choices of colors for cell #1, 3 choices of colors for cell #2, ... all the way up to
	 * cell #6. So that's 3^6 total matrices that stay the same. Therefore, |X_g1| = 3^6.
	 *
	 * 2) If we swap cols 1 and 2, then we have the following transformation:
	 * 123 -> 213
	 * 456    546
	 *
	 * Let's write out the permutation in a single line, as follows:
	 * 123456 -> 213546
	 *
	 * Let's now write them vertically:
	 * (123456)
	 * (213546)
	 *
	 * We can now look at something called cycle of the permutation. This will help us compute
	 * the no. of matrices that remain the same after this permutation.
	 *
	 * 1->2->1. This is one cycle.
	 * 3->3. This is a second cycle. Trivial, since 3 ends up in the same place.
	 * 4->5->4.This is a third cycle. Similar reasoning as 1->2->1 case.
	 * 6->6 is a fourth and final cycle. Trivial.
	 *
	 * So that's a total of 4 cycles. This is important because it tells us that |X_g2| in this case
	 * equals 3^4, where 3 is the total no. of states we're using in this example.
	 *
	 * We can verify this by observation. Recall the permutation:
	 * 123 -> 213
	 * 456    546
	 *
	 * Since indices 3 and 6 are unchanged, it means we can assign any of the 3 states to these. That's 3^2.
	 * Since indices 1,4 are swapped with 2,5 respectively, it means that index 1 MUST equal 2
	 * and index 4 MUST equal 5. With that constraint, we can assign any of the 3 states to indices 1 and 4.
	 * So that's 3^2.
	 * Thus, in total, the total no. of ways to assign states to the cells so that the matrix remains
	 * unchanged after the permutation is: 3^2 * 3^2 = 3^4.
	 *
	 * 3) thru 4): The answers for |X_g3| and |X_g4| are 3^4 each, for the same logic as in 2) above.
	 *
	 * 5) The permutation in this case is as follows:
	 * 123 -> 312
	 * 456    645
	 *
	 * Let's write it in cycle form:
	 * (123456)
	 * (312645)
	 *
	 * And count up the cycles:
	 * 1->3->2->1
	 * 4->6->5->4
	 *
	 * So that's 2 cycles. That means |X_g5| = 3^2. We can verify this by observation the same way we did in 2).
	 *
	 * 6) For the same reason as in 5), |X_g6| = 3^2.
	 *
	 * 7) The permutation in this case is as follows:
	 * 123 -> 456
	 * 456    123
	 *
	 * Cycle notation:
	 * (123456)
	 * (456123)
	 *
	 * Cycles are:
	 * 1->4->1
	 * 2->5->2
	 * 3->6->3
	 *
	 * That's 3 cycles. So |X_g7| = 3^3
	 *
	 * 8) The permutation is as follows:
	 * 123 -> 456 -> 546
	 * 456    123    213
	 *
	 * Cycle notation:
	 * (123456)
	 * (546213)
	 *
	 * Cycles are:
	 * 1->5->1
	 * 2->4->2
	 * 3->6->3
	 *
	 * 3 cycles. Thus |X_g8| = 3^3.
	 *
	 * 9) and 10) Answers are 3^3 each, for same reason as in 8).
	 *
	 * 11) The permutation is as follows:
	 * 123 -> 456 -> 465 -> 645
	 * 456    123    132    312
	 *
	 * (123456)
	 * (645312)
	 * 1 -> 6 -> 2 -> 4 -> 3 -> 5 -> 1
	 *
	 * That's ONE cycle. So |X_g11| = 3^1
	 *
	 * 12) Answer is 3^1 for same reason as in 11)
	 *
	 * ================================================
	 * So, via Burnside's lemma:
	 *
	 * No. of distinct grids
	 * = 1/|G|    * sum(|X_g| for g in G)
	 * = 1/R!C!   * sum(|X_g| for g in G)
	 * = 1/(2!3!) * (3^6 + 3^4 + 3^4 + 3^4 + 3^2 + 3^2 + 3^3 + 3^3 + 3^3 + 3^3 + 3 + 3)
	 * = 92
	 *
	 *  Thus, 92 is the correct answer for the case where R = 2, C = 3, k = # of colors = 3.
	 * */
	
	/* We will create representations for terms involving multiple variables */
	
	/**
	 * Addition of one or more Terms.
	 */
	public static class Terms {
		ArrayList<Term> aL;

		Terms() {
			aL = new ArrayList<>();
		}

		/**
		 * Combines like terms.
		 */
		void simplify() {
			Collections.sort(this.aL);

			int index = 1;
			while (index < this.aL.size()) {
				Term t1 = aL.get(index - 1);
				Term t2 = aL.get(index);
				if (t1.compareTo(t2) == 0) {
					this.aL.remove(index);
					this.aL.remove(index-1);
					this.aL.add(index-1, new Term(t1.varAL, t1.coeff.add(t2.coeff)));
					continue;
				}
				index++;
			}
			//end while
		}

		@Override
		public String toString() {
			return this.aL.toString();
		}
	}

	/**
	 * A term consisting of one or more variables, along with a coefficient constant that can be represented as a fraction..
	 */
	public static class Term implements Comparable<Term>{
		ArrayList<Variable> varAL;
		Fraction coeff;

		Term() {
			varAL = new ArrayList<>();
			this.coeff = new Fraction(1,1);
		}

		Term(Variable v) {
			this();
			this.varAL.add(v);
		}

		Term(ArrayList<Variable> vList, Fraction coeff) {
			this();
			this.varAL = vList;
			this.coeff = coeff;
		}

		void addVar(Variable v) {
			this.varAL.add(v);
		}

		void setCoeff(Fraction coeff) {
			this.coeff = coeff;
		}

		void simplify() {
			Collections.sort(this.varAL);

			int index = 1;
			while (index < this.varAL.size()) {
				Variable v1 = varAL.get(index - 1);
				Variable v2 = varAL.get(index);
				if (v1.equals(v2)) {
					this.varAL.remove(index);
					this.varAL.remove(index-1);
					this.varAL.add(index-1, new Variable(v1.symbol, v1.value, v1.exponent + v2.exponent, v1.subscript));
					continue;
				}
				index++;
			}
			//end while
		}
		
		/**
		 * Multiplies two terms together
		 * @param t
		 * @return
		 */
		Term multiply(Term t) {
			Term multipliedTerm = new Term();
			multipliedTerm.coeff = this.coeff.multiply(t.coeff);
			multipliedTerm.varAL.addAll(this.varAL);
			multipliedTerm.varAL.addAll(t.varAL);
			multipliedTerm.simplify();
			return multipliedTerm;
		}
		//end multiplyTerm
		
		/**
		 * Multiplies this Term with one or more Terms.
		 * @param terms
		 * @return
		 */
		Terms multiply(Terms terms) {
			if (terms.aL == null || terms.aL.size() == 0) {
				Terms ret = new Terms();
				ret.aL.add(this);
				return ret;
			}
			@SuppressWarnings("unchecked")
			ArrayList<Term> tAL = (ArrayList<Term>)terms.aL.clone();
			for (int i = 0; i < tAL.size(); ++i) {
				Term t = tAL.get(i);
				t = this.multiply(t);
				tAL.remove(i);
				tAL.add(i, t);
			}
			Terms ret = new Terms();
			ret.aL = tAL;
			return ret;
		}
		
		/**
		 * Returns the numerical value of this Term, assuming each variable is assigned a particular value.
		 * @return
		 */
		BigInteger eval() {
			BigInteger value = BigInteger.ONE;
			for (Variable v : this.varAL) {
				if (v.eval() == null) return null;
				value = value.multiply(v.eval());
			}
			return value;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (this.coeff.numerator != this.coeff.denominator) {
				sb.append(this.coeff);
			}
			for (Variable v : this.varAL) {
				sb.append(v.toString() + "*");
			}
			sb.deleteCharAt(sb.length() - 1);	//delete the last "*" character
			return sb.toString();
		}

		@Override
		public int compareTo(Term t2) {
			int index1 = 0;
			int index2 = 0;
			while (index1 < this.varAL.size() && index2 < t2.varAL.size()){
				Variable v1 = this.varAL.get(index1);
				Variable v2 = t2.varAL.get(index2);
				if (v1.compareTo(v2) < 0) return -1;
				if (v1.compareTo(v2) > 0) return 1;
				if (v1.exponent < v2.exponent) return 1;
				if (v1.exponent > v2.exponent) return -1;
				index1++;
				index2++;
			}
			//end while

			if (this.varAL.size() < t2.varAL.size()) return -1;
			if (this.varAL.size() > t2.varAL.size()) return 1;
			return 0;
		}
	}
	//end public static class Term

	/**
	 * Represents a single variable. It has a symbol (i.e. x), numerical value, an exponent value, as well as a subscript.
	 */
	public static class Variable implements Comparable<Variable>{
		String symbol;
		BigInteger value;
		long exponent;
		long subscript;

		Variable(String symbol) {
			this.symbol = symbol;
			this.value = null;
			this.exponent = 1;
			this.subscript = 1;
		}

		Variable(String symbol, BigInteger value, long exponent, long subscript) {
			this(symbol);
			this.value = value;
			this.exponent = exponent;
			this.subscript = subscript;
		}

		void setValue(Long value) {
			this.value = new BigInteger(value+"");
		}

		BigInteger eval() {
			return (this.value == null ? null : this.value.pow((int)exponent));
		}


		@Override
		public String toString() {
			return "(" + this.symbol + this.subscript + ")" + (this.exponent == 1 ? "" : "^" + this.exponent);
		}

		@Override
		public boolean equals(Object v2) {
			if (v2 == null) return false;
			if (!(v2 instanceof Variable)) return false;
			Variable v2Var = (Variable)v2;
			if (this.symbol.compareTo(v2Var.symbol) < 0) return false;
			if (this.symbol.compareTo(v2Var.symbol) > 0) return false;
			if (this.subscript < v2Var.subscript) return false;
			if (this.subscript > v2Var.subscript) return false;
			return true;
		}
		@Override
		public int compareTo(Variable v2) {
			if (this.symbol.compareTo(v2.symbol) < 0) return -1;
			if (this.symbol.compareTo(v2.symbol) > 0) return 1;
			if (this.subscript < v2.subscript) return -1;
			if (this.subscript > v2.subscript) return 1;
			return 0;
		}
	}

	/**
	 * Fraction class.
	 */
	public static class Fraction {
		BigInteger numerator, denominator;

		Fraction(long numer, long denom) {
			this.numerator = new BigInteger(numer+"");
			this.denominator = new BigInteger(denom+"");
		}

		Fraction(BigInteger numer, BigInteger denom) {
			this.numerator = numer;
			this.denominator = denom;
		}

		/**
		 * Simplifies the fraction.
		 */
		void simplify() {
			BigInteger div = gcd(numerator, denominator);
			this.numerator = this.numerator.divide(div);
			this.denominator = this.denominator.divide(div);
		}

		/**
		 * Multiply with another fraction.
		 * @param f
		 * @return
		 */
		Fraction multiply(Fraction f) {
			Fraction retFrac = new Fraction(this.numerator.multiply(f.numerator), this.denominator.multiply(f.denominator));
			retFrac.simplify();
			return retFrac;
		}

		/**
		 * Overloaded methods to multiply this Fraction with a number
		 * @param n
		 * @return
		 */
		Fraction multiply(long n) {
			Fraction retFrac = new Fraction(this.numerator.multiply(new BigInteger(n+"")), this.denominator);
			retFrac.simplify();
			return retFrac;
		}

		Fraction multiply(int n) {
			Fraction retFrac = new Fraction(this.numerator.multiply(new BigInteger(n+"")), this.denominator);
			retFrac.simplify();
			return retFrac;
		}

		Fraction multiply(BigInteger n) {
			Fraction retFrac = new Fraction(this.numerator.multiply(n), this.denominator);
			retFrac.simplify();
			return retFrac;
		}

		/**
		 * Performs a fraction addition.
		 * @param f
		 * @return
		 */
		Fraction add(Fraction f) {
			BigInteger lcm = lcm(this.denominator, f.denominator);
			BigInteger mult1 = lcm.divide(this.denominator);
			BigInteger mult2 = lcm.divide(f.denominator);
			BigInteger numer1 = this.numerator.multiply(mult1);
			BigInteger numer2 = f.numerator.multiply(mult2);
			Fraction ret = new Fraction(numer1.add(numer2), lcm);
			ret.simplify();
			return ret;
		}
	}


	private static Term cycle_prod(Term A, Term B) {
		ArrayList<Variable> A_varAL = A.varAL;
		ArrayList<Variable> B_varAL = B.varAL;
//		System.out.println(A_varAL);
//		System.out.println(B_varAL);
		Term result = null;
		for (Variable a : A_varAL) {
			long subscriptA = a.subscript;
			long exponentA = a.exponent;
			for (Variable b: B_varAL) {
				long subscriptB = b.subscript;
				long exponentB = b.exponent;
				long v_lcm = lcm(subscriptA, subscriptB);
//				System.out.println("v_lcm:" + v_lcm);
//				System.out.println(exponentA * exponentB * subscriptA*subscriptB / v_lcm);
				if (result == null) {
					Variable v = new Variable("a", null, (exponentA * exponentB * subscriptA * subscriptB / v_lcm), v_lcm);
					result = new Term(v);
//					System.out.println("result: " + result);
				} else {
					Variable v = new Variable("a", null, (exponentA * exponentB * subscriptA * subscriptB / v_lcm), v_lcm);
					Term t = new Term(v);
//					System.out.println("result before multiplication: " + result);
					result = result.multiply(t);
//					System.out.println("result after: " + result);
				}
			}
			//end inner for
		}
		//end outer for
		return result;
	}

	private static Terms cycle_symm(int n, int level) {
		if (n == 0) return new Terms();
		Terms terms = new Terms();
		for (int i = 1; i <= n; ++i) {
			Terms terms_temp = cycle_symm(n-i, level + 1);
			Term tv = new Term(new Variable("a", null, 1, i));
			terms_temp = tv.multiply(terms_temp);
			terms.aL.addAll(terms_temp.aL);
		}
		for (Term t : terms.aL) {
			t.setCoeff(t.coeff.multiply(new Fraction(1,n)));
		}
		if (level == 0) {
			terms.simplify();
		}
		return terms;
	}
	private static Terms cycle_symm(int n) {
		return cycle_symm(n, 0);
	}

	private static BigInteger gcd(BigInteger a, BigInteger b) {
		while (b.compareTo(BigInteger.ZERO) > 0) {
			BigInteger temp = new BigInteger(b.toString());
			b = a.mod(b);
			a = new BigInteger(temp.toString());
		}
		return a;
	}

	private static long gcd(long a, long b) {
	    while (b > 0) {
	        long temp = b;
	        b = a % b;
	        a = temp;
	    }
	    return a;
	}

	private static BigInteger lcm(BigInteger a, BigInteger b) {
		return a.multiply(b.divide(gcd(a,b)));
	}
	private static long lcm(long a, long b) {
	    return a * (b / gcd(a, b));
	}

	/**
	 * MAIN SOLUTION.
	 * @param r
	 * @param c
	 * @param k
	 * @return
	 */
	public static String answer(int r, int c, int k) {
		Terms termsA = cycle_symm(r);
		Terms termsB = cycle_symm(c);
//		System.out.println(termsA);
//		System.out.println(termsB);
		Terms result = new Terms();
		for (Term termA : termsA.aL) {
			for (Term termB : termsB.aL) {
				Fraction coeff = termA.coeff.multiply(termB.coeff);
//				System.out.println(coeff);
				Term prod = cycle_prod(termA, termB);
				prod.setCoeff(coeff);
				result.aL.add(prod);
			}
		}

		result.simplify();
//		System.out.println("FINAL RESULT:" + result);
		ArrayList<Fraction> temp = new ArrayList<>();
		for (Term t : result.aL) {
			for (Variable v : t.varAL) {
				v.setValue((long)k);
			}
			temp.add(t.coeff.multiply(t.eval()));
		}
//		System.out.println(temp);
		Fraction ret = new Fraction(0,1);
		for (Fraction f : temp) {
			ret = ret.add(f);
		}
		ret.simplify();
		assert ret.denominator.equals(BigInteger.ONE);
		return ret.numerator+"";
	}

	public static void main(String[] args) {
		System.out.println(L5Q1.answer(2, 2, 2));
		System.out.println(L5Q1.answer(12, 12, 12));
	}
}