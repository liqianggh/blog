package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.PhotoDTO;
import cn.mycookies.pojo.dto.SimpleAlbumDTO;
import cn.mycookies.pojo.vo.TagVO;

import java.util.List;

/**
 * @date 2018-22-29
 * @author Jann Lee
 */
public interface AlbumService {

    /**
     * 查询相册列表
     * @param type 1-只有不包含封面，2-包含封面
     * @return
     */
    ServerResponse<List<SimpleAlbumDTO>> listSimpleAlbums(int type, int limit);

    ServerResponse<List<PhotoDTO>> listAllPhotos(int albumId);

    ServerResponse<TagVO> countAlbumNumber();
}
