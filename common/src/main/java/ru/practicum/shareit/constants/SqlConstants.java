package ru.practicum.shareit.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SqlConstants {
    public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    public static final String REQUEST_HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    public static final String DATA_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
}
