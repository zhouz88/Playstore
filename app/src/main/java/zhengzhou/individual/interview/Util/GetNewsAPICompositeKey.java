package zhengzhou.individual.interview.Util;

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
    public GetNewsAPICompositeKey(@NonNull String app, @NonNull String token, @NonNull String lat, @NonNull String lng) {
        this.app = app;
        this.token = token;
        this.lat = lat;
        this.lng = lng;
    }
}
