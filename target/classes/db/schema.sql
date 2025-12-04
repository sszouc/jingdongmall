-- 这里是数据库的信息
-- 1.用户信息表
CREATE TABLE user
(
    id           INT         NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
    username     VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password     CHAR(64)    NOT NULL COMMENT '密码（SHA256加密）',
    email        VARCHAR(100) UNIQUE COMMENT '邮箱（找回密码）',
    phone        VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    avatar       VARCHAR(255) COMMENT '头像URL',
    gender       TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '性别：0保密，1男，2女',
    birthday     DATE        NOT NULL COMMENT '生日',
    status       TINYINT     NOT NULL CHECK (status IN (1, 0)) COMMENT '状态：1正常，0禁用',
    role         TINYINT     NOT NULL CHECK (role IN (0, 1)) COMMENT '角色：0普通用户，1管理员',
    created_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 2.Token黑名单
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

-- 3.地址表
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

-- 4.商品分类表
CREATE TABLE product_category
(
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '分类主键ID',
    name         VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id    INT          NOT NULL DEFAULT 0 COMMENT '父级分类ID，0表示根分类',
    level        TINYINT      NOT NULL DEFAULT 1 COMMENT '分类层级：1一级，2二级',
    sort_order   INT          NOT NULL DEFAULT 0 COMMENT '排序序号，数字越小越靠前',
    is_active    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    created_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_active (is_active)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '商品分类表';

-- 5.商品主表（SPU表）
CREATE TABLE product
(
    id               INT          NOT NULL AUTO_INCREMENT COMMENT '商品主键ID',
    category_id      INT          NULL COMMENT '分类ID',
    name             VARCHAR(200) NOT NULL COMMENT '商品名称',
    description      TEXT COMMENT '商品描述',
    detail_html      TEXT COMMENT '商品详情HTML',
    main_images      JSON COMMENT '主图数组，JSON格式',
    tags             JSON COMMENT '标签数组，JSON格式',

    -- 所有SKU共用的参数（前端ProductDetail.params）
    model            VARCHAR(100) COMMENT '产品型号',
    os               VARCHAR(100) COMMENT '操作系统',
    positioning      VARCHAR(100) COMMENT '产品定位',
    cpu_model        VARCHAR(100) COMMENT 'CPU型号',
    cpu_series       VARCHAR(100) COMMENT 'CPU系列',
    max_turbo_freq   VARCHAR(50) COMMENT '最高睿频',
    cpu_chip         VARCHAR(100) COMMENT 'CPU芯片',
    screen_size      VARCHAR(50) COMMENT '屏幕尺寸',
    screen_ratio     VARCHAR(50) COMMENT '显示比例',
    resolution       VARCHAR(100) COMMENT '分辨率',
    color_gamut      VARCHAR(100) COMMENT '色域',
    refresh_rate     VARCHAR(50) COMMENT '刷新率',
    ram_type         VARCHAR(50) COMMENT '内存类型',
    ssd_type         VARCHAR(50) COMMENT '硬盘类型',
    gpu_type         VARCHAR(50) COMMENT '显卡类型',
    vram_type        VARCHAR(50) COMMENT '显存类型',
    camera           VARCHAR(100) COMMENT '摄像头',
    wifi             VARCHAR(100) COMMENT '无线网卡',
    bluetooth        VARCHAR(100) COMMENT '蓝牙',
    data_interfaces  VARCHAR(200) COMMENT '数据接口',
    video_interfaces VARCHAR(200) COMMENT '视频接口',
    audio_interfaces VARCHAR(200) COMMENT '音频接口',
    keyboard         VARCHAR(100) COMMENT '键盘',
    face_id          VARCHAR(100) COMMENT '人脸识别',
    weight           VARCHAR(50) COMMENT '重量',
    thickness        VARCHAR(50) COMMENT '厚度',
    software         VARCHAR(200) COMMENT '附带软件',

    is_active        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
    created_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_category_id (category_id),
    INDEX idx_is_active (is_active),
    FULLTEXT INDEX idx_name (name) COMMENT '全文索引用于搜索',
    FOREIGN KEY (category_id) REFERENCES product_category (id) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '商品主表（SPU表）';

CREATE TABLE product_sku
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    product_id    INT            NOT NULL,
    sku_code      VARCHAR(50) UNIQUE,
    price         DECIMAL(10, 2) NOT NULL COMMENT '价格',
    stock         INT            NOT NULL DEFAULT 0 COMMENT '库存',
    sales_count   INT                     DEFAULT 0 COMMENT '销量',

    -- ========== specs 数据（用户选择的规格）==========
    os            VARCHAR(100) COMMENT '操作系统',
    cpu           VARCHAR(100) COMMENT '处理器',
    ram           VARCHAR(50) COMMENT '内存容量',
    storage       VARCHAR(50) COMMENT '存储容量',
    gpu           VARCHAR(100) COMMENT '显卡',

    -- ========== diffParams 数据（SKU特有技术参数）==========
    -- 与 specs 相对应
    -- 例如，如果gpu选择了RTX 5070，那么vram_capacity就会是8GB
    -- 如果cpu选择了Ultra 9 275HX，那么gpu_chip会是NVIDIA® GeForce RTX™ 5070
    -- 如果storage选择了2T SSD，ssd_capacity会是2T(1TB+1TB) SSD

    ssd_capacity  VARCHAR(50) COMMENT '硬盘容量',
    gpu_chip      VARCHAR(100) COMMENT '显卡芯片',
    vram_capacity VARCHAR(50) COMMENT '显存容量',

    is_active     TINYINT(1)              DEFAULT 1,
    created_time  DATETIME                DEFAULT CURRENT_TIMESTAMP,
    updated_time  DATETIME                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '商品规格表（SKU表）';