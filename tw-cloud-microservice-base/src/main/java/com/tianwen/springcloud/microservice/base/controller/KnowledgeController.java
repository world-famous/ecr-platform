package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.KnowledgeMicroApi;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Knowledge;
import com.tianwen.springcloud.microservice.base.entity.KnowledgeTree;
import com.tianwen.springcloud.microservice.base.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/knowledge")
public class KnowledgeController extends AbstractCRUDController<Knowledge> implements KnowledgeMicroApi
{
    @Autowired
    private KnowledgeService knowledgeService;

    public String increment(String s)
    {
        if (s == null)
            s = "00000";
        Matcher m = Pattern.compile("\\d+").matcher(s);
        if (!m.find())
            throw new NumberFormatException();
        String num = m.group();
        int inc = Integer.parseInt(num) + 1;
        String incStr = String.format("%0" + num.length() + "d", inc);
        return  m.replaceFirst(incStr);
    }

    @Override
    public void validate(MethodType methodType, Object p)
    {
        Knowledge entity;
        switch (methodType)
        {
            case ADD:
                entity = (Knowledge)p;
                if (entity.getStatus() == null || entity.getStatus().isEmpty())
                    entity.setStatus("1");

                if (entity.getParentknowledgeid() == null || entity.getParentknowledgeid().isEmpty())
                    entity.setParentknowledgeid("0");

                if (entity.getIseditable() == null || entity.getIseditable().isEmpty())
                    entity.setIseditable("1");

                String maxSibling = knowledgeService.getMaxSequence(entity.getParentknowledgeid());

                maxSibling = increment(maxSibling);

                entity.setSequence(maxSibling);
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
    public Response<Knowledge> update(@RequestBody Knowledge entity)
    {
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));

        int retVal = knowledgeService.updateNotNull(entity);

        Response<Knowledge> resp = new Response<>(entity);
        if (retVal == 1)
            retVal = 200;
        resp.getServerResult().setResultCode(Integer.toString(retVal));

        return resp;
    }

    @Override
    public Response<Knowledge> delete(@PathVariable(value = "id") String id)
    {
        Knowledge entity = knowledgeService.selectByKey(id);

        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("parentknowledgeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getKnowledgeid()));
        queryTree.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        List<Knowledge> children = knowledgeService.search(queryTree).getPageInfo().getList();
        if (children != null)
        {
            for (Knowledge child : children)
                knowledgeService.deleteByPrimaryKey(child.getKnowledgeid());
        }

        knowledgeService.deleteByPrimaryKey(id);

        return new Response<>(entity);
    }

    public void makeChildren(KnowledgeTree parent)
    {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        queryTree.addCondition(new QueryCondition("parentknowledgeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, parent.getKnowledge().getKnowledgeid()));

        List<Knowledge> children = knowledgeService.search(queryTree).getPageInfo().getList();
        for(Knowledge child : children)
        {
            KnowledgeTree item = new KnowledgeTree();
            item.setKnowledge(child);
            item.setLabel(child.getName());
            parent.getChildren().add(item);
            makeChildren(item);
        }
    }

    @Override
    public Response<KnowledgeTree> getTree(@RequestBody QueryTree queryTree) {
        String parentid = "", knowid = "";
        if (queryTree.getQueryCondition("parentknowledgeid") != null)
        {
            try{
                parentid = queryTree.getQueryCondition("parentknowledgeid").getFieldValues()[0].toString();
            } catch (Exception e) {}
        }
        if (queryTree.getQueryCondition("knowledgeid") != null)
        {
            try{
                knowid = queryTree.getQueryCondition("knowledgeid").getFieldValues()[0].toString();
            } catch (Exception e) {}
        }

        List<KnowledgeTree> result = new ArrayList<>();
        List<DictItem> sectionList = knowledgeService.getSchoolSectionList(queryTree);

        for(DictItem section : sectionList) {
            KnowledgeTree sectionTree = new KnowledgeTree();
            sectionTree.setLabel(section.getDictname());

            Knowledge secEntity = new Knowledge();
            secEntity.setSchoolsectionid(section.getDictvalue());secEntity.setSchoolsection(section.getDictname());
            sectionTree.setKnowledge(secEntity);

            List<DictItem> subjectList = knowledgeService.getSubjectList(queryTree);
            for (DictItem subject : subjectList)
            {
                QueryTree searchQuery = new QueryTree();
                searchQuery.addCondition(new QueryCondition("schoolsectionid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, section.getDictvalue()));
                searchQuery.addCondition(new QueryCondition("subjectid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, subject.getDictvalue()));
                searchQuery.addCondition(new QueryCondition("parentknowledgeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, parentid));
                searchQuery.addCondition(new QueryCondition("knowledgeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, knowid));
                searchQuery.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));

                KnowledgeTree subjectTree = new KnowledgeTree();
                subjectTree.setLabel(subject.getDictname());

                Knowledge subEntity = new Knowledge();
                subEntity.setSchoolsectionid(section.getDictvalue());subEntity.setSchoolsection(section.getDictname());
                subEntity.setSubjectid(subject.getDictvalue());subEntity.setSubject(subject.getDictname());
                subjectTree.setKnowledge(subEntity);

                List<Knowledge> data = knowledgeService.search(searchQuery).getPageInfo().getList();
                for (Knowledge item : data) {
                    KnowledgeTree parent = new KnowledgeTree();
                    parent.setLabel(item.getName());
                    parent.setKnowledge(item);
                    makeChildren(parent);
                    subjectTree.getChildren().add(parent);
                }
                sectionTree.getChildren().add(subjectTree);
            }
            result.add(sectionTree);
        }

        return new Response<>(result);
    }

    @Override
    public Response getSchoolSectionList(@RequestBody QueryTree queryTree)
    {
        List<DictItem> sectionList = knowledgeService.getSchoolSectionList(queryTree);
        return new Response<>(sectionList);
    }

    @Override
    public Response getSubjectList(@RequestBody QueryTree queryTree)
    {
        List<DictItem> subjectList = knowledgeService.getSubjectList(queryTree);
        return new Response<>(subjectList);
    }

    @Override
    public Response<Knowledge> getList(@RequestBody QueryTree queryTree) {
        return knowledgeService.search(queryTree);
    }

    @Override
    public Response moveKnowledge(@RequestBody Map<String, Object> param) {
        String fromid = null, toid = null;
        if (param.get("source") != null)
            fromid = param.get("source").toString();
        if (param.get("target") != null)
            toid = param.get("target").toString();

        Response resp = new Response();

        if (StringUtils.isEmpty(fromid) || StringUtils.isEmpty(toid))
        {
            resp.getServerResult().setResultCode(IStateCode.PARAMETER_IS_INVALID);
            resp.getServerResult().setResultCode("参数非法");
        }
        else
        {
            Knowledge src = knowledgeService.selectByKey(fromid);
            Knowledge tar = knowledgeService.selectByKey(toid);

            QueryTree parentQuery = new QueryTree();
            parentQuery.addCondition(new QueryCondition("parentknowledgeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, src.getParentknowledgeid()));
            parentQuery.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));

            List<Knowledge> siblings = knowledgeService.search(parentQuery).getPageInfo().getList();

            String sequence;
            for(Knowledge know : siblings)
            {
                sequence = know.getSequence();
                if (Integer.parseInt(sequence) >= Integer.parseInt(tar.getSequence()) && !know.getKnowledgeid().equals(src.getKnowledgeid()))
                {
                    sequence = increment(sequence);
                    know.setSequence(sequence);
                    know.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
                    knowledgeService.updateNotNull(know);
                }
            }

            src.setSequence(tar.getSequence());
            src.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
            knowledgeService.updateNotNull(src);
        }

        return resp;
    }
}
