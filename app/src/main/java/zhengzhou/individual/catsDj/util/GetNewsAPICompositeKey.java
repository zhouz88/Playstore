package zhengzhou.individual.catsDj.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class GetNewsAPICompositeKey {
    String app;
    String token;
    String lat;
    String lng;

    @Builder
    public GetNewsAPICompositeKey(@NonNull String applicationName, @NonNull String tokenName,
                                  @NonNull String latitude, @NonNull String longitude) {
        this.app = applicationName;
        this.token = tokenName;
        this.lat = latitude;
        this.lng = longitude;
    }
}
