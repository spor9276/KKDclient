package 게임클라이언트;

public class Main {
	LoginView loginView = new LoginView();
	public static void main(String[] args) {
		//new MainView();
		new LoginView();
	}
	public LoginView getLoginView(){
		return loginView;
	}

}
