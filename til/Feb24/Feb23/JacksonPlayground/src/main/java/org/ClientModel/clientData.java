package org.ClientModel;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.ServerModel.serverData;
import org.clientInf.zone;
import org.deserzUtil.abstimeDeser;
import org.deserzUtil.siteItemDeser;
import org.deserzUtil.timeDeser;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class clientData {
    private static final Logger logger = LoggerFactory.getLogger(clientData.class);
    @JsonProperty("id")
    @NotNull
    @Min(value = 1L)
    @Max(value = 100000L)
    private int clientID;

    // the usage of 'groups' helps in having diff validation rules for diff consumers of the DTO as shown below
    @Length(min=1 , max=20 ,groups = {Save.class})
    @Length(min=2 , max=25 , groups = {Insert.class})
    private String item;
    @JsonProperty("site")
    // below is needed to auto validate an object which is a member of the class
    @Valid
    @JsonDeserialize(using = siteItemDeser.class)
    private siteItem sItem;
    private zone baseData;
    // the below time is passed as zulu time, i.e. yyyy-MM-ddTHH:mm:ssZ
    // i store the recieved time as a zoneddatetime using my custom deserializer
    @JsonProperty
    @JsonDeserialize(using = timeDeser.class)
    @Getter
    private ZonedDateTime quote_time;
    // the below time is passed from client in custom format , i.e. yyyy-MM-dd HH:mm:ss'
    // it signifies local time (assumed to be NA time zone)
    // i store the rcvd time as zoneddatetime with the NA time zone
    @JsonProperty
    @Getter
    @JsonDeserialize(using = abstimeDeser.class)
    private ZonedDateTime absolute_time;
    private Map<String,String> extra = new HashMap<>();

    // will override the lombok generated setter
    @JsonSetter("cItem")
    public void setmyItem(String item)
    {
        this.item = item;
    }

    public void printPOJO()
    {
        logger.info("the deserialized POJO:");
        logger.info("id: "+this.getClientID());
        logger.info("item name: "+this.getItem());
        logger.info("supplementary info:");
        this.getExtra().entrySet()
                .forEach(e->logger.info(e.getValue()));
        logger.info("site data output is:"+ this.getSItem().toString());
        logger.info("base data output is:"+ this.getBaseData().toString());
        logger.info("time info is:"+ this.getQuote_time());

    }
    @JsonAnySetter
    public void addAdditionalData(String key, String val)
    {
        this.extra.put(key,val);
    }


}