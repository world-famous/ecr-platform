package com.tianwen.springcloud.microservice.base.controller;

import java.util.List;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.DictItemMicroApi;
import com.tianwen.springcloud.microservice.base.api.DictTypeMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.DictType;
import com.tianwen.springcloud.microservice.base.service.DictItemService;
import com.tianwen.springcloud.microservice.base.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "/dicttype")
public class DictTypeController extends AbstractCRUDController<DictType> implements DictTypeMicroApi
{
    @Autowired
    private DictTypeService dictTypeService;

    @Autowired
    private DictItemService dictItemService;

    @Override
    public void validate(MethodType methodType, Object p)
    {
        switch (methodType)
        {
            case ADD:
                break;
            case DELETE:
                break;
            case GET:
                break;
            case SEARCH:
                break;
            case UPDATE:
                break;
            case BATCHADD:
                break;
            case BATCHDELETEBYENTITY:
                break;
            case BATCHUPDATE:
                break;
            case DELETEBYENTITY:
                break;
            case GETBYENTITY:
                break;
            default:
                break;
        }
    }

    @Override
    public Response<DictType> getList(@RequestBody QueryTree queryTree) {
        return dictTypeService.search(queryTree);
    }

    @Override
    public Response<Integer> getCount(@RequestBody QueryTree queryTree) {
        int count = dictTypeService.getCount(queryTree);
        return new Response<>(count);
    }

    @Override
    public Response<DictType> insert(@RequestBody DictType entity) {

        Integer sortNo = dictTypeService.getMaxSortNo(entity.getParenttypeid());
        if (sortNo == null)
            sortNo = 0;
        sortNo += 1;

        if(!entity.getParenttypeid().equals("0")){
            if(dictTypeService.isExistById(entity.getParenttypeid()) == 0){
                return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.DICTYPE_PARENT_NOT_EXIST);
            }
        }
        if(dictTypeService.isExistById(entity.getDicttypeid())!= 0){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.DICTYPE_ID_EXIST);
        }

        DictType zhCn = new DictType();
        //parameter data
        zhCn.setDicttypeid(entity.getDicttypeid());
        zhCn.setDicttypename(entity.getDicttypename());
        zhCn.setParenttypeid(entity.getParenttypeid());
        zhCn.setDescription(entity.getDescription());
        zhCn.setSortno(sortNo);
        //self-calc data
        zhCn.setIseditable("1");
        zhCn.setLang(IBaseMicroConstants.zh_CN);
        dictTypeService.save(zhCn);

        DictType enUs = new DictType();
        //parameter data
        enUs.setDicttypeid(entity.getDicttypeid());
        enUs.setDicttypename(entity.getDicttypename());
        enUs.setParenttypeid(entity.getParenttypeid());
        enUs.setDescription(entity.getDescription());
        enUs.setSortno(sortNo);
        //self-calc data
        enUs.setIseditable("1");
        enUs.setLang(IBaseMicroConstants.en_US);
        dictTypeService.save(enUs);

        return new Response<>();
    }

    @Override
    public Response<DictType> modify(@RequestBody DictType entity) {
        Response<DictType> response = new Response<>();
        String oldid = entity.getOldid();
        DictType searchKey = new DictType();
        searchKey.setDicttypeid(oldid);
        List<DictType> dictTypes = searchByEntity(searchKey).getPageInfo().getList();
        if (CollectionUtils.isEmpty(dictTypes))
        {
            response.getServerResult().setResultCode("-1");
            response.getServerResult().setResultMessage("");
            return response;
        }

        dictItemService.updateType(oldid, entity.getDicttypeid());
        dictTypeService.doUpdate(entity);

        for(DictType item : dictTypes)
        {
            if (!item.getLang().equals(entity.getLang()))
            {
                item.setOldid(entity.getOldid());
                item.setDicttypeid(entity.getDicttypeid());
                dictTypeService.doUpdate(item);
            }
        }

        response.setResponseEntity(entity);
        return response;
    }

    @Override
    public Response<DictType> remove(@PathVariable(value = "dicttypeid") String dicttypeid) {
        dictTypeService.doRemove(dicttypeid);
        return new Response<>();
    }
}
