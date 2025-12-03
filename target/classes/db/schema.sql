#这里是数据库的信息
#1.用户信息表
CREATE TABLE user
(
    id           INT         NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
    username     VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password     CHAR(64)    NOT NULL COMMENT '密码（SHA256加密）',
    email        VARCHAR(100) UNIQUE COMMENT '邮箱（找回密码）',
    phone        VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    avatar       VARCHAR(255) COMMENT '头像URL',
    gender       TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '性别：0保密，1男，2女',
    status       TINYINT     NOT NULL CHECK (status IN (1, 0)) COMMENT '状态：1正常，0禁用',
    role         TINYINT     NOT NULL CHECK (role IN (0, 1)) COMMENT '角色：0普通用户，1管理员',
    created_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';
#2.商品表
CREATE TABLE product
(
    id           INT            NOT NULL AUTO_INCREMENT COMMENT '商品主键ID',
    category_id  INT            NULL COMMENT '关联分类ID',
    name         VARCHAR(255)   NULL COMMENT '商品名称',
    subtitle     VARCHAR(255) COMMENT '商品副标题',
    main_image   VARCHAR(255) COMMENT '主图URL',
    price        DECIMAL(10, 2) NULL COMMENT '商品售价（单位:元）',
    stock        INT            NULL CHECK (stock >= 0) COMMENT '库存数量',
    sales        INT            NULL CHECK (sales >= 0) COMMENT '销量',
    status       SMALLINT       NOT NULL DEFAULT 0 CHECK (status IN (1, 0)) COMMENT '状态：1上架，0下架',
    description  TEXT           NULL COMMENT '商品详情',
    created_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

#3.商品收藏表
CREATE TABLE product_favorite
(
    id           INT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      INT      NOT NULL COMMENT '用户ID',
    product_id   INT      NOT NULL COMMENT '商品ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品收藏表';

#4.商品分类表
CREATE TABLE category
(
    id           INT         NOT NULL AUTO_INCREMENT COMMENT '分类主键ID',
    name         VARCHAR(50) NULL COMMENT '分类名称',
    parent_id    INT         NOT NULL CHECK (parent_id >= 0) COMMENT '父分类ID：0为一级分类',
    sort         INT         NOT NULL CHECK (sort >= 0) COMMENT '排序权重',
    status       TINYINT     NOT NULL DEFAULT 0 CHECK (status IN (1, 0)) COMMENT '状态：1启用，0禁用',
    created_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类表';

#5.Token黑名单
CREATE TABLE token_blacklist
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id      INT             NOT NULL COMMENT '用户ID',
    token_hash   VARCHAR(255)    NOT NULL COMMENT '令牌哈希（完整token的SHA256）',
    expires_at   DATETIME        NOT NULL COMMENT '令牌过期时间',
    reason       VARCHAR(50)     NOT NULL DEFAULT 'LOGOUT' COMMENT '加入黑名单原因：LOGOUT登出、PWD_CHANGE密码修改、SECURITY安全原因',
    created_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    -- 唯一约束防止重复添加
    UNIQUE KEY uk_token_hash (token_hash)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '令牌黑名单表';

#6.地址表
CREATE TABLE user_address
(
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '地址主键ID',
    user_id      INT          NOT NULL COMMENT '用户ID',
    name         VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    phone        VARCHAR(20)  NOT NULL COMMENT '收货人手机号',
    province     VARCHAR(50)  NOT NULL COMMENT '省份',
    city         VARCHAR(50)  NOT NULL COMMENT '城市',
    district     VARCHAR(50)  NOT NULL COMMENT '区县',
    detail       VARCHAR(255) NOT NULL COMMENT '详细地址',
    postal_code  VARCHAR(10) COMMENT '邮政编码',
    is_default   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认地址：1是，0否',
    status       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：1有效，0删除/无效',
    created_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    -- 外键关联用户表
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '用户收货地址表';