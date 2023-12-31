package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class HitDto {
    @NotBlank
    @Size(max = 2000, message = "uri is more than 2000 symbols")
    private String uri;
    @NotBlank
    @Size(max = 200, message = "appName is more than 200 symbols")
    private String app;
    @NotBlank
    @Size(max = 50, message = "ip is more than 50 symbols")
    private String ip;
    @NotNull(message = "timestamp is null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @PastOrPresent
    private LocalDateTime timestamp;
}
