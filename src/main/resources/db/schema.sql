-- 这里是数据库的信息
-- 1.用户信息表
CREATE TABLE user
(
    id           INT         NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
    username     VARCHAR(50) NOT NULL COMMENT '用户名',
    password     CHAR(64)    NOT NULL COMMENT '密码（SHA256加密）',
    email        VARCHAR(100) UNIQUE COMMENT '邮箱（找回密码）',
    phone        VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    avatar       VARCHAR(255) COMMENT '头像URL',
    gender       TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '性别：0保密，1男，2女',
    birthday     DATE        NOT NULL COMMENT '生日',
    status       TINYINT     NOT NULL CHECK (status IN (1, 0)) COMMENT '状态：1正常，0禁用',
    role         TINYINT     NOT NULL CHECK (role IN (0, 1, 2)) COMMENT '角色：0普通用户，1管理员，2超级管理员',
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

-- 6.具体产品表
CREATE TABLE product_sku
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    product_id    INT            NOT NULL,
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
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '具体产品表（SKU表）';

-- 7.购物车表
CREATE TABLE shopping_cart
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车主键ID',
    user_id      INT             NOT NULL COMMENT '用户ID',
    sku_id       INT             NOT NULL COMMENT 'SKU ID',
    quantity     INT             NOT NULL DEFAULT 1 COMMENT '商品数量',
    selected     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '是否选中：1选中，0未选中',
    created_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_sku (user_id, sku_id), -- 每个用户每个SKU只能有一条记录
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (sku_id) REFERENCES product_sku (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '购物车表';

-- 8.订单主表
CREATE TABLE `order`
(
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单主键ID',
    order_sn             VARCHAR(32)     NOT NULL UNIQUE COMMENT '订单编号（唯一）',
    user_id              INT             NOT NULL COMMENT '用户ID',
    total_amount         DECIMAL(10, 2)  NOT NULL COMMENT '订单总金额',
    discount_amount      DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    shipping_fee         DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '运费',
    pay_amount           DECIMAL(10, 2)  NOT NULL COMMENT '实付金额 = total_amount - discount_amount + shipping_fee',

    -- 收货地址信息（快照，防止地址修改影响历史订单）
    receiver_name        VARCHAR(50)     NOT NULL COMMENT '收货人姓名',
    receiver_phone       VARCHAR(20)     NOT NULL COMMENT '收货人手机号',
    receiver_province    VARCHAR(50)     NOT NULL COMMENT '省份',
    receiver_city        VARCHAR(50)     NOT NULL COMMENT '城市',
    receiver_district    VARCHAR(50)     NOT NULL COMMENT '区县',
    receiver_detail      VARCHAR(255)    NOT NULL COMMENT '详细地址',
    receiver_postal_code VARCHAR(10) COMMENT '邮政编码',

    -- 订单状态
    status               TINYINT         NOT NULL CHECK (status IN (0, 1, 2, 3, 4, 5, 6, 7)) COMMENT '订单状态：0待付款，1待发货，2待收货，3已完成，4已取消，5退款中，6退款成功，7退款失败',

    -- 支付信息
    payment_method       TINYINT         NOT NULL DEFAULT 0 COMMENT '支付方式：0未支付，1支付宝，2微信支付，3银行卡',
    pay_time             DATETIME COMMENT '支付时间',
    transaction_id       VARCHAR(64) COMMENT '第三方支付交易号',

    -- 物流信息
    shipping_method      VARCHAR(50) COMMENT '配送方式',
    tracking_number      VARCHAR(100) COMMENT '快递单号',
    shipping_time        DATETIME COMMENT '发货时间',
    confirm_time         DATETIME COMMENT '确认收货时间',

    -- 取消/退款信息
    cancel_time          DATETIME COMMENT '取消时间',
    cancel_reason        VARCHAR(255) COMMENT '取消原因',
    refund_time          DATETIME COMMENT '退款时间',
    refund_reason        VARCHAR(255) COMMENT '退款原因',

    -- 备注
    buyer_remark         VARCHAR(500) COMMENT '买家留言',
    admin_remark         VARCHAR(500) COMMENT '管理员备注',

    created_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_sn (order_sn),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '订单主表';

-- 9.订单商品明细表
CREATE TABLE order_item
(
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单商品主键ID',
    order_id          BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    sku_id            INT             NOT NULL COMMENT 'SKU ID',
    product_name      VARCHAR(200)    NOT NULL COMMENT '商品名称（快照）',
    sku_specs         JSON            NOT NULL COMMENT 'SKU规格（快照）',
    main_image        VARCHAR(255)    NOT NULL COMMENT '商品主图（快照）',
    price             DECIMAL(10, 2)  NOT NULL COMMENT '商品单价（快照）',
    quantity          INT             NOT NULL COMMENT '购买数量',
    total_price       DECIMAL(10, 2)  NOT NULL COMMENT '商品总价 = price * quantity',

    -- 售后状态
    after_sale_status TINYINT         NOT NULL DEFAULT 0 COMMENT '售后状态：0无售后，1退款中，2退款成功，3退款失败，4换货中，5换货成功',

    created_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    INDEX idx_order_id (order_id),
    INDEX idx_sku_id (sku_id),
    FOREIGN KEY (order_id) REFERENCES `order` (id) ON DELETE CASCADE,
    FOREIGN KEY (sku_id) REFERENCES product_sku (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '订单商品明细表';

-- 10.轮播图数据库
CREATE TABLE carousel
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    image_url   VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_url    VARCHAR(500) COMMENT '点击跳转链接',
    sort_order  INT      DEFAULT 0 COMMENT '排序序号，数字越小越靠前',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_sort (sort_order)
) COMMENT ='轮播图表';