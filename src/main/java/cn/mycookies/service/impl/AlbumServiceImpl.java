package cn.mycookies.service.impl;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.AlbumDOMapper;
import cn.mycookies.dao.PhotoDOMapper;
import cn.mycookies.pojo.dto.PhotoDTO;
import cn.mycookies.pojo.dto.SimpleAlbumDTO;
import cn.mycookies.pojo.vo.TagVO;
import cn.mycookies.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @date 2018-11-29
 * @author Jann Lee
 */
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    AlbumDOMapper albumDOMapper;

    @Autowired
    PhotoDOMapper photoDOMapper;

    @Override
    public ServerResponse<List<SimpleAlbumDTO>> listSimpleAlbums(int type, int limit) {
        if (type == 1){

        } else {

        }


        return null;
    }

    @Override
    public ServerResponse<List<PhotoDTO>> listAllPhotos(int albumId) {
        return null;
    }

    @Override
    public ServerResponse<TagVO> countAlbumNumber() {
        return null;
    }
}
