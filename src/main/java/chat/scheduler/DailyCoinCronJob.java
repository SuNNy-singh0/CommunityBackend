package chat.scheduler;

import chat.enitity.UserDetail;
import chat.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyCoinCronJob {

    @Autowired
    private UserDetailRepository userDetailRepository;

    // Runs at 11:59 PM every day
    @Scheduled(cron = "59 59 23 * * *")
    public void updateMonthlyPerformance() {
        List<UserDetail> users = userDetailRepository.findAll();

        for (UserDetail user : users) {
            user.pushDailyCoinsToMonthly(); // Move coins to monthly performance
            userDetailRepository.save(user);
        }

        System.out.println("✅ Daily coins added to monthly performance & reset at midnight!");
    }
    @Scheduled(cron = "59 59 23 L * ?")
    public void resetMonthlyPerformance() {
        List<UserDetail> users = userDetailRepository.findAll();

        for (UserDetail user : users) {
            user.setMonthlyPerformance(null); // Reset monthly performance
            userDetailRepository.save(user);
        }

        System.out.println("✅ Monthly performance reset at midnight on the last day of the month!");
    }
}
