package cn.mycookies.pojo.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class SimpleAlbumDTO {

    private Short id;

    private String name;

    private String albumDesc;

    private String coverImg;
}
