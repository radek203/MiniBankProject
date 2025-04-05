package me.radek203.branchservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "branch")
public class AppProperties {

    private int id;

    public int getBranchId() {
        return id;
    }

}
