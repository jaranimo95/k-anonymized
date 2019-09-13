public class kAnonymizedData 
{
		private String zipLo, zipHi, breed;
		private boolean disability;
		private int numOwners;

		public kAnonymizedData() {
			zipLo = null;
			zipHi = null;
			breed = null;
			disability = false;
			numOwners = 0;
		}

		public kAnonymizedData(String zl, String zh, String b, boolean d, int n) {
			zipLo = zl;
			zipHi = zh;
			breed = b;
			disability = d;
			numOwners = n;
		}

		public String getZipLo() {
			return zipLo;
		}

		public void setZipLo(String zl) {
			zipLo = zl;
		}

		public String getZipHi() {
			return zipHi;
		}

		public void setZipHi(String zh) {
			zipHi = zh;
		}

		public String getBreed() {
			return breed;
		}

		public void setBreed(String b) {
			breed = b;
		}

		public boolean getDisability() {
			return disability;
		}

		public void setDisability(boolean d) {
			disability = d;
		}

		public int getNumOwners() {
			return numOwners;
		}

		public void setNumOwners(int n) {
			numOwners = n;
		}

	}