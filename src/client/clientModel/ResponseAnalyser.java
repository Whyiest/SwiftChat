package client.clientModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseAnalyser {



    public List<User> assignmentCharacteristics(String serverResponse) {
        String[] data = serverResponse.split(";");
        List<User> userList = new ArrayList<>();
        int caractPerUser = 10;
        for(int i = 0; i<data.length; i += caractPerUser) {
            User user = new User();
            user.setId(Integer.parseInt(data[i]));
            user.setPermission(data[i+1]);
            user.setFirstName(data[i+2]);
            user.setLastName(data[i+3]);
            user.setUserName(data[i+4]);
            user.setMail(data[i+5]);
            user.setPassword(data[i+6]);
            user.setLastConnectionTime(LocalDateTime.parse(data[i+7]));
            user.setStatus(data[i+8]);
            user.setBanned(Boolean.parseBoolean(data[i+9]));
            userList.add(user);
        }
        return userList;
    }

}
