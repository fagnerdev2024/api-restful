package med.voll.api.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class CustomHealthIndicator implements HealthIndicator {


    @Override
    public Health health() {
        boolean isHealthy = checkCustomHealthSaude();
        if(isHealthy){
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    private boolean checkCustomHealthSaude(){
        return true;
    }
}
