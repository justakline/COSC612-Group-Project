package traveldart.entities;

public class userDTO {
    
    private String username;
    private String dateOfBirth;


    public userDTO(String username, String dateOfBirth){
        this.username = username;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String newUsername){
        this.username = newUsername;
    }

    public String getDateOfBirth(){
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String newDate){
        this.dateOfBirth = newDate;
    }
}