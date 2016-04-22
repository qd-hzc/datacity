package com.city.resourcecategory.analysis.text.entity;

import javax.persistence.*;

/**
 * Created by wxl on 2016/3/14.
 * 标签关联表
 */
@Entity
@Table(name = "RC_TEXT_LABEL_LINK")
public class TextLabelLink {
    @Id
    @Column
    @SequenceGenerator(name = "rcTextLabelLink", sequenceName = "RC_TEXT_LINK_SEQ")
    @GeneratedValue(generator = "rcTextLabelLink")
    private Integer id;
    @OneToOne
    @JoinColumn(name = "LABEL_ID")
    private TextLabel label;//标签
    @Column(name = "CONTENT_ID")
    private Integer contentId;//分析内容

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TextLabel getLabel() {
        return label;
    }

    public void setLabel(TextLabel label) {
        this.label = label;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }
}
