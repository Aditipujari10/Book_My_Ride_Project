

public class User {
    private int userId;
    private String name;
    private String email;
    private String password; 

    public User(int userId, String name, String email, String password) { 
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    
    public boolean login(String inputPass) { 
        return this.password.equals(inputPass);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId);
    }

    public void displayUser() {
        System.out.println("User ID: " + userId + " | Name: " + name + " | Email: " + email);
    }

    public String getEmail() {
		// TODO Auto-generated method stub
		return this.email; 
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password; 
	}
}