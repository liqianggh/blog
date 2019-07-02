package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.AlbumDOMapper;
import cn.mycookies.dao.PhotoDOMapper;
import cn.mycookies.pojo.dto.PhotoDTO;
import cn.mycookies.pojo.dto.SimpleAlbumDTO;
import cn.mycookies.pojo.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Jann Lee
 * @date 2018-11-29
 */
public class AlbumService {

    @Autowired
    AlbumDOMapper albumDOMapper;

    @Autowired
    PhotoDOMapper photoDOMapper;

    public ServerResponse<List<SimpleAlbumDTO>> listSimpleAlbums(int type, int limit) {
        if (type == 1) {

        } else {

        }


        return null;
    }

    public ServerResponse<List<PhotoDTO>> listAllPhotos(int albumId) {
        return null;
    }

    public ServerResponse<TagVO> countAlbumNumber() {
        return null;
    }
}
