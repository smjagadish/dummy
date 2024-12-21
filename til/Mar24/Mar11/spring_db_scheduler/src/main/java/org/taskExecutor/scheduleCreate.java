package org.taskExecutor;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.AbstractTask;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class scheduleCreate {
    @Getter
    private final Scheduler scheduler;
    private Set<String> taskName = new HashSet<>();
    private List<AbstractTask<?>> taskList = new ArrayList<>();
    private Task<?>[] tarr;
    @Autowired
    public scheduleCreate(@Qualifier("taskarray") final Task<?>[] taskArray , final DataSource ds)
    {

        this.tarr = taskArray;
        scheduler = Scheduler
                .create(ds,tarr)
                .threads(1)
                .build();
    }
    @PostConstruct
    public void startTask()
    {
        scheduler.start();
        for (int i =0; i<tarr.length;i++)
        {
            int val = i+10;
            if(tarr[i].getDataClass()== Integer.class && tarr[i].getTaskName() == "one_time_with_data")
            {
                scheduler.schedule(new TaskInstance<Integer>(tarr[i].getTaskName(),tarr[i].getTaskName(),val),Instant.now().plusSeconds(80));
            } else if (tarr[i].getTaskName().startsWith("recurring")) {
                // do nothing
            }
            else
            scheduler.schedule(tarr[i].instance(tarr[i].getTaskName()),Instant.now().plusSeconds(90));
        }
        //scheduler.schedule(taskList.get(0).instance(taskName.stream().limit(1).collect(Collectors.toList()).get(0)),Instant.now().plusSeconds(90));
    }
}