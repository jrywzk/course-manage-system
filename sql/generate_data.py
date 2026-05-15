#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
sms 数据库测试数据生成脚本
========================
为 schema-v2.sql (course_selection_v3) 生成大规模测试数据。

要求:
  - 11张表, 平均每表 >= 5,000条记录
  - 至少2张表 > 50,000条 (t_enrollment, t_score)

使用: python generate_data.py
输出: generated_data.sql (同目录)
导入: 先执行 schema-v2.sql 建库和种子数据, 再 mysql -u root -p sms < generated_data.sql
"""

import random
import time
import os

random.seed(20250514)

# ============================================================
# 配置 — 种子数据 ID 上限 (来自 schema-v2.sql 7.1~7.11)
# ============================================================
SEED = {
    "department": 5,
    "classroom": 7,
    "user": 14,
    "major": 6,
    "admin": 2,
    "teacher": 4,
    "student": 8,
    "course": 8,
    "course_section": 10,
    "enrollment": 22,
    "score": 14,
}

# 目标总量 (含种子)
TARGET = {
    "department": 20,       # 真实大学院系数
    "classroom": 500,       # 教室 (扩大规模)
    "major": 80,            # 专业 (每系4个左右)
    "user": 12548,          # = admin(20) + teacher(500) + student(12028)
    "admin": 20,
    "teacher": 500,
    "student": 12028,       # 种子8 + 新增12020
    "course": 398,          # 种子8 + 新增90 + 额外300
    "course_section": 6000,  # 实际由结构化生成决定 (~6000)
    "enrollment": 60022,    # >50,000 ✓  种子22 + 新增60000
    "score": 55014,         # >50,000 ✓  种子14 + 新增55000
}

# 新增数量
GEN = {k: TARGET[k] - SEED[k] for k in TARGET}

# 辅助ID起始
NEXT_ID = {k: SEED[k] + 1 for k in SEED}

OUTPUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "generated_data.sql")

# ============================================================
# 真实数据素材
# ============================================================

SURNAMES = [
    "王","李","张","刘","陈","杨","黄","赵","吴","周",
    "徐","孙","马","朱","胡","郭","何","高","林","郑",
    "罗","梁","谢","宋","唐","许","韩","冯","邓","曹",
    "彭","曾","萧","田","董","潘","袁","蔡","蒋","余",
    "于","杜","叶","程","苏","魏","吕","丁","任","沈",
]

MALE_GIVEN = [
    "伟","强","磊","勇","军","鹏","杰","涛","明","超",
    "建华","志强","文博","浩然","子涵","宇轩","梓豪","一鸣",
    "天宇","思远","俊杰","博文","子豪","明哲","嘉诚","睿",
    "海峰","阳","辉","斌","翔","峰","霖","毅","恒","铭",
    "晨","旭","宁","致远","景辉","瑞","浩宇",
]

FEMALE_GIVEN = [
    "芳","秀英","敏","静","丽","婷","雪","娟","艳","娜",
    "雨桐","梓涵","思雨","欣怡","诗涵","梦瑶","佳琪","晓萱",
    "雅婷","雨萱","若曦","美琳","慧敏","晓红","丽华","玲",
    "洁","雯","怡","萍","颖","莉","燕","丹","蕾","彤",
    "兰","凤","云","桂英","秀兰","玉兰","海燕",
]

# 新增院系 (种子已有: CS/MATH/ENG/PHYS/ECON)
NEW_DEPARTMENTS = [
    ("CHEM",   "化学与分子工程学院",   "010-62780006"),
    ("HIST",   "历史学院",             "010-62780007"),
    ("ART",    "艺术学院",             "010-62780008"),
    ("LAW",    "法学院",               "010-62780009"),
    ("MED",    "医学院",               "010-62780010"),
    ("MECH",   "机械工程学院",         "010-62780011"),
    ("EE",     "电子与信息工程学院",   "010-62780012"),
    ("CIVIL",  "土木工程学院",         "010-62780013"),
    ("BIO",    "生命科学学院",         "010-62780014"),
    ("MAT",    "材料科学与工程学院",   "010-62780015"),
    ("ENERGY", "能源与动力工程学院",   "010-62780016"),
    ("AERO",   "航空航天学院",         "010-62780017"),
    ("ENV",    "环境科学与工程学院",   "010-62780018"),
    ("MGMT",   "管理学院",             "010-62780019"),
    ("PHIL",   "哲学系",               "010-62780020"),
]

NEW_MAJORS = [
    # CS
    ("CS", "CS-03", "人工智能"),
    ("CS", "CS-04", "信息安全"),
    ("CS", "CS-05", "数据科学与大数据技术"),
    ("CS", "CS-06", "物联网工程"),
    # MATH
    ("MATH", "MATH-02", "信息与计算科学"),
    ("MATH", "MATH-03", "统计学"),
    ("MATH", "MATH-04", "金融数学"),
    # ENG
    ("ENG", "ENG-02", "商务英语"),
    ("ENG", "ENG-03", "翻译"),
    ("ENG", "ENG-04", "日语"),
    # PHYS
    ("PHYS", "PHYS-02", "应用物理学"),
    ("PHYS", "PHYS-03", "光电信息科学与工程"),
    # ECON
    ("ECON", "ECON-02", "会计学"),
    ("ECON", "ECON-03", "金融学"),
    ("ECON", "ECON-04", "国际经济与贸易"),
    # 新院系
    ("CHEM", "CHEM-01", "化学"),
    ("CHEM", "CHEM-02", "应用化学"),
    ("CHEM", "CHEM-03", "材料化学"),
    ("HIST", "HIST-01", "历史学"),
    ("HIST", "HIST-02", "考古学"),
    ("ART", "ART-01", "音乐学"),
    ("ART", "ART-02", "美术学"),
    ("LAW", "LAW-01", "法学"),
    ("LAW", "LAW-02", "知识产权"),
    ("MED", "MED-01", "临床医学"),
    ("MED", "MED-02", "基础医学"),
    ("MECH", "MECH-01", "机械工程"),
    ("MECH", "MECH-02", "车辆工程"),
    ("MECH", "MECH-03", "智能制造工程"),
    ("EE", "EE-01", "电子信息工程"),
    ("EE", "EE-02", "通信工程"),
    ("EE", "EE-03", "微电子科学与工程"),
    ("CIVIL", "CIVIL-01", "土木工程"),
    ("CIVIL", "CIVIL-02", "建筑学"),
    ("BIO", "BIO-01", "生物科学"),
    ("BIO", "BIO-02", "生物工程"),
    ("MAT", "MAT-01", "材料科学与工程"),
    ("MAT", "MAT-02", "高分子材料与工程"),
    ("ENERGY", "ENERGY-01", "能源与动力工程"),
    ("AERO", "AERO-01", "航空航天工程"),
    ("ENV", "ENV-01", "环境工程"),
    ("MGMT", "MGMT-01", "工程管理"),
    ("MGMT", "MGMT-02", "信息管理与信息系统"),
    ("PHIL", "PHIL-01", "哲学"),
    # === 新增30个专业 (50→80) ===
    ("CS", "CS-07", "网络工程"),
    ("CS", "CS-08", "数字媒体技术"),
    ("CS", "CS-09", "空间信息与数字技术"),
    ("MATH", "MATH-05", "应用统计学"),
    ("MATH", "MATH-06", "数理统计"),
    ("ENG", "ENG-05", "德语"),
    ("ENG", "ENG-06", "法语"),
    ("PHYS", "PHYS-04", "核物理"),
    ("ECON", "ECON-05", "经济学"),
    ("ECON", "ECON-06", "人力资源管理"),
    ("ECON", "ECON-07", "物流管理"),
    ("CHEM", "CHEM-04", "化学生物学"),
    ("HIST", "HIST-03", "世界史"),
    ("ART", "ART-03", "设计学"),
    ("LAW", "LAW-03", "国际法"),
    ("MED", "MED-03", "药学"),
    ("MED", "MED-04", "护理学"),
    ("MED", "MED-05", "公共卫生"),
    ("MECH", "MECH-04", "工业设计"),
    ("EE", "EE-04", "自动化"),
    ("EE", "EE-05", "电磁场与无线技术"),
    ("CIVIL", "CIVIL-03", "给排水科学与工程"),
    ("CIVIL", "CIVIL-04", "城乡规划"),
    ("BIO", "BIO-03", "生物信息学"),
    ("MAT", "MAT-03", "纳米材料与技术"),
    ("ENERGY", "ENERGY-02", "新能源科学与工程"),
    ("AERO", "AERO-02", "飞行器设计"),
    ("ENV", "ENV-02", "环境科学"),
    ("MGMT", "MGMT-03", "电子商务"),
    ("PHIL", "PHIL-02", "逻辑学"),
]

NEW_COURSES = [
    # CS 学院
    ("CS","CS-102","计算机组成原理",3.5,56,"必修","计算机硬件系统与指令集架构"),
    ("CS","CS-205","操作系统",4.0,64,"必修","进程管理、内存管理与文件系统"),
    ("CS","CS-202","计算机网络",3.5,56,"必修","TCP/IP协议栈与网络应用开发"),
    ("CS","CS-302","软件工程导论",3.0,48,"必修","软件开发流程、UML与敏捷方法"),
    ("CS","CS-408","算法设计与分析",3.0,48,"选修","分治、动态规划、贪心与NP理论"),
    ("CS","CS-402","人工智能导论",3.0,48,"选修","搜索、知识表示、机器学习基础"),
    ("CS","CS-403","机器学习",3.0,48,"选修","监督、无监督学习与深度学习入门"),
    ("CS","CS-404","Web前端开发",2.0,32,"选修","HTML/CSS/JavaScript与React"),
    ("CS","CS-405","移动应用开发",2.0,32,"选修","Android与iOS应用开发基础"),
    ("CS","CS-406","云计算与大数据",2.0,32,"选修","Hadoop/Spark生态系统"),
    ("CS","CS-407","网络安全技术",2.0,32,"选修","加解密、防火墙与入侵检测"),
    # MATH 学院
    ("MATH","MATH-102","高等数学(下)",5.0,80,"必修","多元微积分、无穷级数"),
    ("MATH","MATH-210","线性代数",3.0,48,"必修","矩阵、向量空间与特征值"),
    ("MATH","MATH-202","概率论与数理统计",3.0,48,"必修","随机变量、分布与假设检验"),
    ("MATH","MATH-301","数值分析",3.0,48,"选修","数值逼近、数值积分与微分方程数值解"),
    ("MATH","MATH-302","运筹学",3.0,48,"选修","线性规划、整数规划与网络优化"),
    ("MATH","MATH-401","数学建模",2.0,32,"选修","实际问题建模与竞赛训练"),
    # ENG 学院
    ("ENG","ENG-102","大学英语(二)",3.0,48,"必修","中级英语综合能力提升"),
    ("ENG","ENG-210","大学英语(三)",3.0,48,"必修","学术英语阅读与写作"),
    ("ENG","ENG-202","大学英语(四)",3.0,48,"必修","跨文化交际与高级英语"),
    ("ENG","ENG-301","英语写作",2.0,32,"选修","学术论文与应用文写作"),
    ("ENG","ENG-302","英语口译",2.0,32,"选修","交替传译与同声传译基础"),
    ("ENG","ENG-401","英美文学选读",2.0,32,"选修","英美经典文学作品赏析"),
    # PHYS 学院
    ("PHYS","PHYS-102","大学物理(下)",4.0,64,"必修","光学、量子物理基础"),
    ("PHYS","PHYS-201","理论力学",3.0,48,"必修","拉格朗日力学与哈密顿力学"),
    ("PHYS","PHYS-202","量子力学",4.0,64,"必修","波函数、薛定谔方程"),
    ("PHYS","PHYS-301","固体物理",3.0,48,"选修","晶格结构、能带理论"),
    ("PHYS","PHYS-302","天体物理导论",2.0,32,"选修","恒星演化、宇宙学基础"),
    # ECON 学院
    ("ECON","ECON-102","宏观经济学",3.0,48,"必修","国民收入、货币政策与经济增长"),
    ("ECON","ECON-201","管理学原理",3.0,48,"必修","计划、组织、领导与控制"),
    ("ECON","ECON-202","基础会计学",3.0,48,"必修","借贷记账法与财务报表"),
    ("ECON","ECON-301","市场营销学",3.0,48,"选修","市场细分、定位与营销组合"),
    ("ECON","ECON-302","财务管理",3.0,48,"选修","资本预算、融资与股利政策"),
    ("ECON","ECON-401","国际贸易实务",2.0,32,"选修","贸易术语与合同管理"),
    # 其他学院
    ("CHEM","CHEM-101","无机化学",3.0,48,"必修","元素周期律与配位化学"),
    ("CHEM","CHEM-102","有机化学",4.0,64,"必修","官能团反应与有机合成"),
    ("CHEM","CHEM-201","分析化学",3.0,48,"必修","定量分析与仪器分析"),
    ("CHEM","CHEM-202","物理化学",4.0,64,"必修","热力学、动力学与电化学"),
    ("HIST","HIST-101","中国古代史",3.0,48,"必修","先秦至明清中国历史"),
    ("HIST","HIST-102","中国近现代史",3.0,48,"必修","1840年以来中国历史"),
    ("HIST","HIST-201","世界古代史",3.0,48,"必修","古代文明与世界帝国"),
    ("ART","ART-101","素描基础",2.0,32,"必修","静物、人物素描技法"),
    ("ART","ART-102","色彩构成",2.0,32,"必修","色彩理论与创作实践"),
    ("ART","ART-201","中外美术史",3.0,48,"必修","中西方美术发展历程"),
    ("LAW","LAW-101","宪法学",3.0,48,"必修","宪法原理与基本权利"),
    ("LAW","LAW-102","民法学",4.0,64,"必修","民法总则、物权与合同"),
    ("LAW","LAW-201","刑法学",4.0,64,"必修","犯罪构成与刑罚理论"),
    ("MED","MED-101","人体解剖学",4.0,64,"必修","人体结构与器官系统"),
    ("MED","MED-102","生理学",4.0,64,"必修","人体功能与调节机制"),
    ("MECH","MECH-101","机械原理",3.5,56,"必修","机构学与机械动力学"),
    ("MECH","MECH-102","机械设计",3.5,56,"必修","机械零件设计与强度计算"),
    ("EE","EE-101","电路分析",3.0,48,"必修","电路基本定律与分析方法"),
    ("EE","EE-102","数字电路",3.0,48,"必修","组合与时序逻辑电路设计"),
    ("CIVIL","CIVIL-101","结构力学",4.0,64,"必修","静定与超静定结构分析"),
    ("CIVIL","CIVIL-102","工程制图",3.0,48,"必修","建筑制图标准与CAD"),
    ("BIO","BIO-101","分子生物学",3.0,48,"必修","DNA复制、转录与翻译"),
    ("BIO","BIO-102","细胞生物学",3.0,48,"必修","细胞结构与功能"),
    ("MAT","MAT-101","材料科学基础",3.0,48,"必修","晶体结构、相图与相变"),
    ("ENERGY","ENERGY-101","工程热力学",4.0,64,"必修","热力学定律与动力循环"),
    ("AERO","AERO-101","空气动力学",3.5,56,"必修","流体力学与飞行原理"),
    ("ENV","ENV-101","环境工程原理",3.0,48,"必修","水/气/固废处理原理"),
    ("MGMT","MGMT-101","项目管理",3.0,48,"必修","项目计划、执行与控制"),
    ("MGMT","MGMT-102","供应链管理",3.0,48,"选修","采购、库存与物流优化"),
    ("PHIL","PHIL-101","西方哲学史",3.0,48,"必修","古希腊至当代西方哲学"),
    # === 新增18门课程 (补足到108门) ===
    ("CS","CS-501","编译原理",3.5,56,"选修","词法分析、语法分析与代码生成"),
    ("CS","CS-502","计算机图形学",3.0,48,"选修","图形渲染管线与三维建模"),
    ("MATH","MATH-402","数学史",2.0,32,"选修","数学思想发展历程"),
    ("ENG","ENG-402","英语国家概况",2.0,32,"选修","英美国家历史文化概况"),
    ("ECON","ECON-402","计量经济学",3.0,48,"选修","经济数据建模与实证分析"),
    ("CHEM","CHEM-301","高分子化学",3.0,48,"选修","高分子合成与性能表征"),
    ("HIST","HIST-301","中国思想史",3.0,48,"选修","先秦诸子至近现代思想演变"),
    ("LAW","LAW-301","经济法",3.0,48,"选修","市场规制法与宏观调控法"),
    ("MED","MED-201","病理学",3.5,56,"必修","疾病发生发展机制"),
    ("MED","MED-202","药理学",3.0,48,"必修","药物作用机制与临床应用"),
    ("EE","EE-201","信号与系统",3.5,56,"必修","连续与离散信号系统分析"),
    ("CIVIL","CIVIL-201","混凝土结构",3.5,56,"必修","混凝土构件与结构设计"),
    ("BIO","BIO-201","遗传学",3.0,48,"必修","基因传递、表达与变异"),
    ("MAT","MAT-201","材料力学性能",3.0,48,"必修","材料力学行为与断裂"),
    ("ENERGY","ENERGY-201","传热学",3.5,56,"必修","导热、对流与辐射传热"),
    ("AERO","AERO-201","飞行器结构力学",3.5,56,"必修","飞行器结构分析与设计"),
    ("ENV","ENV-201","水污染控制工程",3.0,48,"必修","污水处理技术与工艺"),
    ("PHIL","PHIL-201","中国哲学史",3.0,48,"必修","先秦至近现代中国哲学"),
]

# 公选课
PUBLIC_ELECTIVES = [
    ("GEN-101","大学生心理健康",1.0,16,"公选","心理健康知识与自我调适"),
    ("GEN-102","形势与政策",1.0,16,"公选","国内外形势分析与政策解读"),
    ("GEN-201","体育(一)",1.0,32,"公选","体能训练与体育技能"),
    ("GEN-202","体育(二)",1.0,32,"公选","专项体育技能训练"),
    ("GEN-301","职业规划与就业指导",1.0,16,"公选","职业生涯规划与求职技能"),
    ("GEN-302","创新与创业基础",2.0,32,"公选","创新思维与创业实践"),
    ("GEN-401","中国传统文化概论",2.0,32,"公选","儒释道思想与文化传承"),
    ("GEN-402","信息技术基础",2.0,32,"公选","计算机基础与办公软件"),
    ("GEN-501","文献检索与利用",1.0,16,"公选","学术资源检索方法与工具"),
    ("GEN-502","书法艺术",1.0,16,"公选","毛笔与硬笔书法技法"),
    ("GEN-601","影视鉴赏",1.0,16,"公选","经典影视作品分析与鉴赏"),
    ("GEN-602","音乐欣赏",1.0,16,"公选","中西方经典音乐作品赏析"),
    ("GEN-701","社交礼仪",1.0,16,"公选","现代社交场合礼仪规范"),
    ("GEN-702","演讲与口才",1.0,16,"公选","公众演讲技巧与表达训练"),
    ("GEN-801","环境与可持续发展",1.0,16,"公选","生态文明与可持续发展"),
    ("GEN-802","急救与健康常识",1.0,16,"公选","常见急症处理与健康管理"),
    ("GEN-901","Python程序设计",2.0,32,"公选","Python语言与数据处理"),
    ("GEN-902","摄影技术",1.0,16,"公选","数码摄影基础与构图"),
]

SEMESTERS = [
    "2022-2023-1","2022-2023-2",
    "2023-2024-1","2023-2024-2",
    "2024-2025-1","2024-2025-2",
    "2025-2026-1","2025-2026-2",
]

BUILDINGS = [
    "第一教学楼","第二教学楼","第三教学楼","第四教学楼",
    "逸夫楼","综合教学楼","实验楼A","实验楼B",
    "信息楼","文科楼","理科楼","工科楼",
    "经管楼","外语楼","艺术楼","图书馆",
    "大学生活动中心",
]

SCHEDULE_DAYS = ["周一","周二","周三","周四","周五"]
SCHEDULE_SLOTS = ["1-2节","3-4节","5-6节","7-8节","9-10节"]
TITLES = ["教授"]*3 + ["副教授"]*8 + ["讲师"]*7 + ["助教"]*2
ENROLLMENT_SOURCES = ["自主选课"]*6 + ["系统预置"]*2 + ["教师代选","补选","重修选课"]


# ============================================================
# 辅助函数
# ============================================================

def chinese_name(gender=None):
    if gender is None:
        gender = random.choice(["M","F"])
    s = random.choice(SURNAMES)
    g = random.choice(MALE_GIVEN if gender == "M" else FEMALE_GIVEN)
    return s + g

def phone():
    p = ["138","139","137","136","135","158","159","186","188","150","152","156"]
    return random.choice(p) + "".join(str(random.randint(0,9)) for _ in range(8))

def email(prefix, idx):
    return f"{prefix}{idx}@university.edu.cn"

def schedule():
    n = random.choices([1,2,3], weights=[10,75,15])[0]
    parts = random.sample(
        [f"{d} {s}" for d in SCHEDULE_DAYS for s in SCHEDULE_SLOTS], n)
    return ", ".join(sorted(parts))

def gpa(score):
    if score >= 90: return 4.0
    elif score >= 85: return 3.7
    elif score >= 82: return 3.3
    elif score >= 78: return 3.0
    elif score >= 75: return 2.7
    elif score >= 72: return 2.3
    elif score >= 68: return 2.0
    elif score >= 64: return 1.5
    elif score >= 60: return 1.0
    return 0.0

def esc(v):
    if v is None: return "NULL"
    if isinstance(v, (int, float)): return str(v)
    return "'" + str(v).replace("\\","\\\\").replace("'","\\'") + "'"

def batch_insert(f, table, columns, rows, size=1000):
    c = ", ".join(columns)
    for i in range(0, len(rows), size):
        b = rows[i:i+size]
        vals = ",\n".join("(" + ", ".join(esc(v) for v in r) + ")" for r in b)
        f.write(f"INSERT INTO {table} ({c}) VALUES\n{vals};\n\n")


# ============================================================
# 主流程
# ============================================================

def main():
    t0 = time.time()
    print("sms 测试数据生成器")
    print(f"输出: {OUTPUT}")
    print()

    with open(OUTPUT, "w", encoding="utf-8") as f:
        def w(s=""): f.write(s + "\n")

        w("-- ============================================================")
        w("-- 大规模测试数据 (由 generate_data.py 自动生成)")
        w(f"-- 时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
        w("-- 请先执行 schema-v2.sql，再导入本文件")
        w("-- ============================================================")
        w()
        w("USE sms;")
        w()
        w("SET FOREIGN_KEY_CHECKS = 0;")
        w("SET UNIQUE_CHECKS = 0;")
        w("SET AUTOCOMMIT = 0;")
        w()
        w("-- 暂删触发器以加速批量插入")
        w("DROP TRIGGER IF EXISTS trg_enrollment_insert;")
        w("DROP TRIGGER IF EXISTS trg_enrollment_update;")
        w("DROP TRIGGER IF EXISTS trg_enrollment_delete;")
        w("DROP TRIGGER IF EXISTS trg_score_before_insert;")
        w("DROP TRIGGER IF EXISTS trg_score_before_update;")
        w()

        # ────────────────────────────────────────────────
        # 1. t_department (从5 → 20, 新增15条)
        # ────────────────────────────────────────────────
        print("[1/11] t_department ...", end=" ")
        rows = [(d[0], d[1], d[2], 1) for d in NEW_DEPARTMENTS]
        batch_insert(f, "t_department",
                     ["department_code","department_name","office_phone","status"], rows)
        f.write("COMMIT;\n\n")
        print(f"+{len(rows)} = {SEED['department'] + len(rows)} 条")

        # 建立 dept_code → dept_id 映射 (含种子)
        seed_depts = [
            ("CS","计算机科学与技术学院"),
            ("MATH","数学与统计学院"),
            ("ENG","外国语学院"),
            ("PHYS","物理学院"),
            ("ECON","经济管理学院"),
        ]
        all_dept_code_id = {}
        for i, (code, name) in enumerate(seed_depts):
            all_dept_code_id[code] = i + 1
        for i, d in enumerate(NEW_DEPARTMENTS):
            all_dept_code_id[d[0]] = SEED["department"] + 1 + i

        # ────────────────────────────────────────────────
        # 2. t_major (从6 → 80, 新增74条)
        # ────────────────────────────────────────────────
        print("[2/11] t_major ...", end=" ")
        rows = []
        mid = NEXT_ID["major"]
        for dept_code, m_code, m_name in NEW_MAJORS:
            rows.append((all_dept_code_id[dept_code], m_code, m_name, 1))
        batch_insert(f, "t_major", ["department_id","major_code","major_name","status"], rows)
        f.write("COMMIT;\n\n")
        all_major_ids = list(range(1, SEED["major"] + 1 + len(rows)))
        print(f"+{len(rows)} = {len(all_major_ids)} 条")

        # ────────────────────────────────────────────────
        # 3. t_classroom (从7 → 500, 新增493条)
        # ────────────────────────────────────────────────
        print("[3/11] t_classroom ...", end=" ")
        rows = []
        cid = NEXT_ID["classroom"]
        for i in range(GEN["classroom"]):
            building = BUILDINGS[i % len(BUILDINGS)]
            floor = (i // len(BUILDINGS)) + 1
            room_num = (i % 20) + 1
            room_no = f"{floor}{room_num:02d}"
            cap = random.choice([30,40,45,60,80,100,120,150,200])
            remark = random.choice(["多媒体教室","阶梯教室","普通教室","机房","实验室","报告厅","语音室","研讨室"])
            rows.append((building, room_no, cap, 1, remark))
        batch_insert(f, "t_classroom",
                     ["building","room_no","capacity","status","remark"], rows)
        f.write("COMMIT;\n\n")
        all_classroom_ids = list(range(1, SEED["classroom"] + 1 + len(rows)))
        print(f"+{len(rows)} = {len(all_classroom_ids)} 条")

        # ────────────────────────────────────────────────
        # 预计算: major_id → dept_code 映射 (用于生成有意义的学号)
        # ────────────────────────────────────────────────
        major_dept_map = {
            1: "CS", 2: "CS", 3: "MATH", 4: "ENG", 5: "PHYS", 6: "ECON"
        }
        for i, (dept_code, m_code, m_name) in enumerate(NEW_MAJORS):
            major_dept_map[SEED["major"] + 1 + i] = dept_code

        # 预计算教师数据 (含工号: {dept_code}{year}{seq})
        teacher_dept_counters = {}
        teacher_rows_data = []
        teacher_usernames = []

        for i in range(GEN["teacher"]):
            dc = list(all_dept_code_id.keys())
            dept_code = dc[(i + 4) % len(dc)]
            dept_id = all_dept_code_id[dept_code]
            teacher_dept_counters[dept_code] = teacher_dept_counters.get(dept_code, 0) + 1
            seq = teacher_dept_counters[dept_code]
            teacher_no = f"T{dept_code}{2025:04d}{seq:04d}"
            name = chinese_name()
            title = random.choice(TITLES)
            ph = phone()
            em = email("tch", i+5)
            teacher_rows_data.append((dept_id, teacher_no, name, title, ph, em))
            teacher_usernames.append(teacher_no)

        # 预计算学生数据 (含学号: {dept_code}{enrollment_year}{seq})
        year_pool = [2022]*3000 + [2023]*3000 + [2024]*3000 + [2025]*3020
        random.shuffle(year_pool)

        student_dept_year_counters = {}
        student_rows_data = []
        student_usernames = []

        for i in range(GEN["student"]):
            mid = all_major_ids[(i + 8) % len(all_major_ids)]
            dept_code = major_dept_map[mid]
            g = random.choice(["M","F"])
            ey = year_pool[i]
            key = (dept_code, ey)
            student_dept_year_counters[key] = student_dept_year_counters.get(key, 0) + 1
            seq = student_dept_year_counters[key]
            student_no = f"S{dept_code}{ey:04d}{seq:04d}"
            name = chinese_name(g)
            ph = phone()
            em = email("stu", i+9)
            student_rows_data.append((mid, student_no, name, g, ph, em, ey))
            student_usernames.append(student_no)

        # 预计算教师姓名映射 (teacher_id → teacher_name, 供后续 graded_by 使用)
        teacher_name_map = {}
        # 种子教师
        seed_teacher_names = ["王教授", "刘副教授", "陈讲师", "赵教授"]
        for i, nm in enumerate(seed_teacher_names):
            teacher_name_map[i + 1] = nm
        # 生成教师
        for i, td in enumerate(teacher_rows_data):
            teacher_name_map[SEED["teacher"] + 1 + i] = td[2]

        # ────────────────────────────────────────────────
        # 4. t_user (从14 → 12548, 新增12534条)
        # ────────────────────────────────────────────────
        print("[4/11] t_user ...", end=" ")
        uid = NEXT_ID["user"]
        admin_uids, teacher_uids, student_uids = [], [], []

        rows = []
        # 管理员 — username 保持 admin+数字 格式
        for i in range(GEN["admin"]):
            rows.append((f"admin{i+3:02d}", "123456", "admin", 1, "2024-01-01 00:00:00"))
            admin_uids.append(uid); uid += 1
        # 教师 — username 使用工号 (= teacher_no)
        for tno in teacher_usernames:
            rows.append((tno, "123456", "teacher", 1, "2024-01-01 00:00:00"))
            teacher_uids.append(uid); uid += 1
        # 学生 — username 使用学号 (= student_no)
        for sno in student_usernames:
            rows.append((sno, "123456", "student", 1, "2024-09-01 00:00:00"))
            student_uids.append(uid); uid += 1

        batch_insert(f, "t_user",
                     ["username","password","role","status","created_at"], rows)
        f.write("COMMIT;\n\n")
        print(f"+{len(rows)} = 12548 条")

        # ────────────────────────────────────────────────
        # 5. t_admin (从2 → 20, 新增18条)
        # ────────────────────────────────────────────────
        print("[5/11] t_admin ...", end=" ")
        rows = []
        for i, u in enumerate(admin_uids):
            rows.append((u, f"admin{i+3:02d}", chinese_name(),
                         phone(), email("admin", i+3), 1))
        batch_insert(f, "t_admin",
                     ["user_id","admin_no","admin_name","phone","email","status"], rows)
        f.write("COMMIT;\n\n")
        print(f"+{len(rows)} = {SEED['admin'] + len(rows)} 条 (admins)")

        # ────────────────────────────────────────────────
        # 6. t_teacher (从4 → 500, 新增496条)
        # ────────────────────────────────────────────────
        print("[6/11] t_teacher ...", end=" ")
        rows = []
        for i, u in enumerate(teacher_uids):
            dept_id, teacher_no, name, title, ph, em = teacher_rows_data[i]
            rows.append((u, dept_id, teacher_no, name, title, ph, em, 1))
        batch_insert(f, "t_teacher",
                     ["user_id","department_id","teacher_no","teacher_name",
                      "title","phone","email","status"], rows)
        f.write("COMMIT;\n\n")
        all_teacher_ids = list(range(1, SEED["teacher"] + 1 + len(rows)))
        print(f"+{len(rows)} = {len(all_teacher_ids)} 条")

        # ────────────────────────────────────────────────
        # 7. t_student (从8 → 12028, 新增12020条)
        # ────────────────────────────────────────────────
        print("[7/11] t_student ...")
        rows = []
        for i, u in enumerate(student_uids):
            mid, student_no, name, g, ph, em, ey = student_rows_data[i]
            rows.append((u, mid, student_no, name, g, ph, em, ey, 1))
        batch_insert(f, "t_student",
                     ["user_id","major_id","student_no","student_name","gender",
                      "phone","email","enrollment_year","status"], rows, size=2000)
        f.write("COMMIT;\n\n")
        all_student_ids = list(range(1, SEED["student"] + 1 + len(rows)))
        # 学生入学年份映射 (用于选课)
        student_years = {}
        for i, sid in enumerate(range(SEED["student"] + 1, SEED["student"] + 1 + len(rows))):
            student_years[sid] = student_rows_data[i][6]
        # 种子学生年份
        for sid, yr in [(1,2025),(2,2025),(3,2025),(4,2025),
                         (5,2025),(6,2025),(7,2025),(8,2025)]:
            student_years[sid] = yr
        print(f"  +{len(rows)} = {len(all_student_ids)} 条")

        # ────────────────────────────────────────────────
        # 8. t_course (从8 → 108, 新增100条)
        # ────────────────────────────────────────────────
        print("[8/11] t_course ...", end=" ")
        all_new_courses = NEW_COURSES + [("CS",) + c for c in PUBLIC_ELECTIVES]
        rows = []
        # 将 PUBLIC_ELECTIVES 的 dept_code 都映射到 CS (通识课归教务处/CS管理)
        for dept_code, code, name, credit, hours, ctype, desc in all_new_courses:
            did = all_dept_code_id.get(dept_code, all_dept_code_id["CS"])
            rows.append((did, code, name, credit, hours, ctype, desc, 1))
        batch_insert(f, "t_course",
                     ["department_id","course_code","course_name","credit",
                      "total_hours","course_type","description","status"], rows)
        f.write("COMMIT;\n\n")
        all_course_ids = list(range(1, SEED["course"] + 1 + len(rows)))
        print(f"+{len(rows)} = {len(all_course_ids)} 条")

        # 为满足教学班约束, 在各院系下程序化生成额外课程 (每院系15门, 共300门)
        extra_courses = []
        extra_suffixes = [
            ("前沿专题", 2.0, 32, "选修"), ("实验课", 1.5, 48, "必修"),
            ("实践项目", 2.0, 32, "选修"), ("概论", 2.0, 32, "选修"),
            ("高级专题", 2.0, 32, "选修"), ("案例研讨", 2.0, 32, "选修"),
            ("创新设计", 2.0, 32, "选修"), ("综合实训", 1.5, 48, "必修"),
            ("学科前沿", 2.0, 32, "选修"), ("研究方法", 2.0, 32, "选修"),
            ("技术应用", 2.0, 32, "选修"), ("基础强化", 2.0, 32, "选修"),
            ("专业英语", 2.0, 32, "选修"), ("工程实践", 1.5, 48, "必修"),
            ("毕业设计导论", 1.0, 16, "选修"),
        ]
        for dept_code, dept_id in all_dept_code_id.items():
            for j, (suffix, credit, hours, ctype) in enumerate(extra_suffixes):
                extra_courses.append((
                    dept_code,
                    f"{dept_code}-9{j+1:02d}",
                    f"{dept_code}院系{suffix}",
                    credit, hours, ctype,
                    f"{dept_code}院系{suffix}课程"
                ))
        all_extra_courses = NEW_COURSES + [("CS",) + c for c in PUBLIC_ELECTIVES] + extra_courses
        extra_only = extra_courses
        rows2 = []
        for dept_code, code, name, credit, hours, ctype, desc in extra_only:
            did = all_dept_code_id.get(dept_code, all_dept_code_id["CS"])
            rows2.append((did, code, name, credit, hours, ctype, desc, 1))
        batch_insert(f, "t_course",
                     ["department_id","course_code","course_name","credit",
                      "total_hours","course_type","description","status"], rows2)
        f.write("COMMIT;\n\n")
        all_course_ids = list(range(1, SEED["course"] + 1 + len(all_new_courses) + len(extra_courses)))
        print(f"  +{len(rows2)} 额外课程, 总计 {len(all_course_ids)} 门")

        # ────────────────────────────────────────────────
        # 9. t_course_section (1-3个班/每门课/每学期)
        # ────────────────────────────────────────────────
        print("[9/11] t_course_section ...")
        # 每门课在每个学期开 1-3 个教学班, 用计数器约束
        rows = []
        sem_pool = SEMESTERS
        course_sem_count = {}  # (course_id, semester) → 已开教学班数

        for cid in all_course_ids:
            # 每门课在随机数量的学期中开课
            n_sems = random.randint(3, len(sem_pool))
            chosen_sems = random.sample(sem_pool, min(n_sems, len(sem_pool)))
            for sem in chosen_sems:
                key = (cid, sem)
                n = random.choices([1, 2, 3], weights=[30, 55, 15])[0]
                for _ in range(n):
                    tid = random.choice(all_teacher_ids)
                    clid = random.choice(all_classroom_ids)
                    sec_code = f"SEC{SEED['course_section'] + 1 + len(rows):05d}"
                    sch = schedule()
                    cap = random.choice([30, 40, 45, 60, 80, 100, 120, 150])
                    rows.append((cid, tid, clid, sec_code, sem, sch, cap, 0, 1))

        batch_insert(f, "t_course_section",
                     ["course_id","teacher_id","classroom_id","section_code",
                      "semester","schedule_text","capacity_limit","selected_count","status"],
                     rows, size=2000)
        f.write("COMMIT;\n\n")
        all_section_ids = list(range(1, SEED["course_section"] + 1 + len(rows)))
        print(f"  +{len(rows)} = {len(all_section_ids)} 条")

        # 按学期组织教学班 & 建立 section→semester & section→teacher 映射
        sections_by_sem = {}
        section_semester_map = {}
        section_teacher_map = {}  # section_id → teacher_name (供成绩 graded_by 使用)
        for i, row in enumerate(rows):
            sid = SEED["course_section"] + 1 + i
            sem = row[4]
            sections_by_sem.setdefault(sem, []).append(sid)
            section_semester_map[sid] = sem
            section_teacher_map[sid] = teacher_name_map.get(row[1], f"教师{row[1]:05d}")
        # 种子教学班也加入
        seed_sections = [
            (1,"2025-2026-1"),(2,"2025-2026-1"),(3,"2025-2026-1"),
            (4,"2025-2026-1"),(5,"2025-2026-1"),(6,"2025-2026-1"),
            (7,"2025-2026-1"),(8,"2025-2026-1"),(9,"2025-2026-1"),
            (10,"2025-2026-1"),
        ]
        # 种子教学班的教师映射 (来自种子数据)
        seed_sec_teachers = {
            1: "王教授", 2: "刘副教授", 3: "王教授", 4: "刘副教授",
            5: "刘副教授", 6: "陈讲师", 7: "陈讲师", 8: "赵教授",
            9: "王教授", 10: "赵教授",
        }
        for sid, sem in seed_sections:
            sections_by_sem.setdefault(sem, []).append(sid)
            section_semester_map[sid] = sem
            section_teacher_map[sid] = seed_sec_teachers.get(sid, "未知教师")

        # ────────────────────────────────────────────────
        # 10. t_enrollment (从22 → 60022, 新增60000条)
        # ────────────────────────────────────────────────
        print("[10/11] t_enrollment ...")
        # 排重
        existing_pairs = {
            (1,1),(1,3),(1,6),(1,7),(2,2),(2,4),(2,5),(2,8),
            (3,1),(3,5),(3,6),(4,6),(4,8),(5,6),(5,7),(6,8),(7,8),
            (8,1),(8,3),(8,6),(1,9),(3,9),
        }
        used = set(existing_pairs)
        rows = []
        goal = GEN["enrollment"]
        eid = NEXT_ID["enrollment"]
        enrollment_meta = {}  # enrollment_id → semester (供成绩生成使用)
        enrollment_section_map = {}  # enrollment_id → section_id (供 graded_by 使用)

        # 每个学生在其可用的学期中选课
        def semesters_for_enrollment_year(yr):
            return [s for s in SEMESTERS if s >= f"{yr}-{yr+1}-1"]

        for sid in all_student_ids:
            if len(rows) >= goal:
                break
            ey = student_years.get(sid, 2025)
            sems = semesters_for_enrollment_year(ey)
            for sem in sems:
                if len(rows) >= goal:
                    break
                if sem not in sections_by_sem:
                    continue
                avail = sections_by_sem[sem]
                if not avail:
                    continue
                # 每学期选5-9门课
                n = random.choices([5,6,7,8,9], weights=[15,30,30,15,10])[0]
                n = min(n, len(avail))
                chosen = random.sample(avail, n)
                for sec_id in chosen:
                    if len(rows) >= goal:
                        break
                    pair = (sid, sec_id)
                    if pair in used:
                        continue
                    used.add(pair)
                    # 选课时间
                    y = int(sem.split("-")[0])
                    m = 9 if sem.endswith("-1") else 2
                    d = random.randint(1, 14)
                    h = random.randint(8, 20)
                    mi = random.randint(0, 59)
                    s = random.randint(0, 59)
                    st = f"{y}-{m:02d}-{d:02d} {h:02d}:{mi:02d}:{s:02d}"
                    src = random.choice(ENROLLMENT_SOURCES)
                    rows.append((eid, sid, sec_id, st, 1, src, None))
                    enrollment_meta[eid] = section_semester_map.get(sec_id, sem)
                    enrollment_section_map[eid] = sec_id
                    eid += 1

        # 如果还不够，随机补选 (含学期合法性校验)
        while len(rows) < goal:
            sid = random.choice(all_student_ids)
            sec_id = random.choice(all_section_ids)
            pair = (sid, sec_id)
            if pair in used:
                continue
            sem = section_semester_map.get(sec_id, "2025-2026-1")
            # 校验学生入学年份是否允许选该学期的课
            ey = student_years.get(sid, 2025)
            valid_sems = semesters_for_enrollment_year(ey)
            if sem not in valid_sems:
                continue
            used.add(pair)
            y = int(sem.split("-")[0])
            m = 9 if sem.endswith("-1") else 2
            st = f"{y}-{m:02d}-{random.randint(1,14):02d} {random.randint(8,20):02d}:{random.randint(0,59):02d}:00"
            rows.append((eid, sid, sec_id, st, 1, random.choice(ENROLLMENT_SOURCES), None))
            enrollment_meta[eid] = sem
            enrollment_section_map[eid] = sec_id
            eid += 1

        batch_insert(f, "t_enrollment",
                     ["enrollment_id","student_id","section_id","select_time",
                      "status","source","remark"], rows, size=2000)
        f.write("COMMIT;\n\n")
        all_enrollment_ids = list(range(1, SEED["enrollment"] + len(rows) + 1))
        # 只取新生成的 enrollment_id
        new_enrollment_ids = list(range(NEXT_ID["enrollment"], NEXT_ID["enrollment"] + len(rows)))
        print(f"  +{len(rows)} = {SEED['enrollment'] + len(rows)} 条")

        # ────────────────────────────────────────────────
        # 11. t_score (从14 → 55014, 新增55000条)
        # ────────────────────────────────────────────────
        print("[11/11] t_score ...")
        goal = GEN["score"]
        # 从新 enrollment 中选前 goal 个生成成绩
        scored_eids = new_enrollment_ids[:goal]
        random.shuffle(scored_eids)
        rows = []

        for enr_id in scored_eids:
            # 成绩分布: 正态分布均值72, 标准差14
            final = round(random.gauss(72, 14), 2)
            final = max(0, min(100, final))
            usual = round(final + random.gauss(5, 8), 2)
            usual = max(0, min(100, usual))
            exam = round((final - usual * 0.3) / 0.7, 2)
            exam = max(0, min(100, exam))
            final = round(usual * 0.3 + exam * 0.7, 2)
            gp = gpa(final)
            passed = 1 if final >= 60 else 0

            # 成绩录入时间: 根据选课学期推算 (期末后1-2个月)
            sem = enrollment_meta.get(enr_id, "2025-2026-1")
            sem_year = int(sem.split("-")[0])
            # 秋季学期 → 次年1-2月录入, 春季学期 → 当年6-7月录入
            if sem.endswith("-1"):
                gy = sem_year + 1
                gm = random.choice([1, 2])
            else:
                gy = sem_year + 1
                gm = random.choice([6, 7])
            gd = random.randint(1, 28)
            gh = random.randint(8, 18)
            gmi = random.randint(0, 59)
            g_at = f"{gy}-{gm:02d}-{gd:02d} {gh:02d}:{gmi:02d}:00"
            g_by = section_teacher_map.get(
                enrollment_section_map.get(enr_id, 0),
                f"教师{enrollment_section_map.get(enr_id, 0):05d}"
            )

            rows.append((
                enr_id, usual, exam, final, gp, passed, g_at, g_by
            ))

        batch_insert(f, "t_score",
                     ["enrollment_id","usual_score","exam_score","final_score",
                      "gpa_point","is_passed","graded_at","graded_by"], rows, size=2000)
        f.write("COMMIT;\n\n")
        print(f"  +{len(rows)} = {SEED['score'] + len(rows)} 条")

        # ────────────────────────────────────────────────
        # 同步 selected_count
        # ────────────────────────────────────────────────
        print("同步 selected_count ...", end=" ")
        w("-- 同步教学班已选人数")
        w("UPDATE t_course_section cs")
        w("SET selected_count = (")
        w("    SELECT COUNT(*) FROM t_enrollment e")
        w("    WHERE e.section_id = cs.section_id AND e.status = 1")
        w(");")
        w("COMMIT;")
        w()

        # ────────────────────────────────────────────────
        # 重建触发器
        # ────────────────────────────────────────────────
        w("-- 重建触发器")
        w("DELIMITER //")
        w()
        w("CREATE TRIGGER trg_enrollment_insert")
        w("AFTER INSERT ON t_enrollment FOR EACH ROW")
        w("BEGIN")
        w("    IF NEW.status = 1 THEN")
        w("        UPDATE t_course_section")
        w("        SET selected_count = selected_count + 1")
        w("        WHERE section_id = NEW.section_id;")
        w("    END IF;")
        w("END //")
        w()
        w("CREATE TRIGGER trg_enrollment_update")
        w("AFTER UPDATE ON t_enrollment FOR EACH ROW")
        w("BEGIN")
        w("    IF OLD.status = 1 AND NEW.status = 0 THEN")
        w("        UPDATE t_course_section")
        w("        SET selected_count = selected_count - 1")
        w("        WHERE section_id = NEW.section_id;")
        w("    ELSEIF OLD.status = 0 AND NEW.status = 1 THEN")
        w("        UPDATE t_course_section")
        w("        SET selected_count = selected_count + 1")
        w("        WHERE section_id = NEW.section_id;")
        w("    END IF;")
        w("END //")
        w()
        w("CREATE TRIGGER trg_enrollment_delete")
        w("AFTER DELETE ON t_enrollment FOR EACH ROW")
        w("BEGIN")
        w("    IF OLD.status = 1 THEN")
        w("        UPDATE t_course_section")
        w("        SET selected_count = selected_count - 1")
        w("        WHERE section_id = OLD.section_id;")
        w("    END IF;")
        w("END //")
        w()
        w("CREATE TRIGGER trg_score_before_insert")
        w("BEFORE INSERT ON t_score FOR EACH ROW")
        w("BEGIN")
        w("    IF NEW.final_score IS NOT NULL THEN")
        w("        SET NEW.is_passed = IF(NEW.final_score >= 60, 1, 0);")
        w("    END IF;")
        w("END //")
        w()
        w("CREATE TRIGGER trg_score_before_update")
        w("BEFORE UPDATE ON t_score FOR EACH ROW")
        w("BEGIN")
        w("    IF NEW.final_score IS NOT NULL THEN")
        w("        SET NEW.is_passed = IF(NEW.final_score >= 60, 1, 0);")
        w("    END IF;")
        w("END //")
        w()
        w("DELIMITER ;")
        w()

        # 恢复设置
        w("SET FOREIGN_KEY_CHECKS = 1;")
        w("SET UNIQUE_CHECKS = 1;")
        w("SET AUTOCOMMIT = 1;")
        w()

        # 验证查询
        w("-- ============================================================")
        w("-- 数据验证")
        w("-- ============================================================")
        w("SELECT 't_department' AS tbl, COUNT(*) AS cnt FROM t_department")
        w("UNION ALL SELECT 't_classroom', COUNT(*) FROM t_classroom")
        w("UNION ALL SELECT 't_major', COUNT(*) FROM t_major")
        w("UNION ALL SELECT 't_user', COUNT(*) FROM t_user")
        w("UNION ALL SELECT 't_admin', COUNT(*) FROM t_admin")
        w("UNION ALL SELECT 't_teacher', COUNT(*) FROM t_teacher")
        w("UNION ALL SELECT 't_student', COUNT(*) FROM t_student")
        w("UNION ALL SELECT 't_course', COUNT(*) FROM t_course")
        w("UNION ALL SELECT 't_course_section', COUNT(*) FROM t_course_section")
        w("UNION ALL SELECT 't_enrollment', COUNT(*) FROM t_enrollment")
        w("UNION ALL SELECT 't_score', COUNT(*) FROM t_score;")
        w()

    # ── 统计 (使用实际生成数量) ──
    elapsed = time.time() - t0
    size_mb = os.path.getsize(OUTPUT) / (1024 * 1024)

    # 获取实际生成的 enrollment 和 score 记录数
    enrollment_total = len(all_enrollment_ids)
    score_total = SEED["score"] + GEN["score"]

    actual = {
        "department": len(all_dept_code_id),
        "classroom": len(all_classroom_ids),
        "major": len(all_major_ids),
        "user": SEED["user"] + GEN["admin"] + GEN["teacher"] + GEN["student"],
        "admin": SEED["admin"] + GEN["admin"],
        "teacher": len(all_teacher_ids),
        "student": len(all_student_ids),
        "course": len(all_course_ids),
        "course_section": len(all_section_ids),
        "enrollment": enrollment_total,
        "score": score_total,
    }
    total = sum(actual.values())
    avg = total / len(actual)
    big = sum(1 for v in actual.values() if v > 50000)

    print()
    print("=" * 55)
    print(f"{'Table':<22s} {'Records':>10s}")
    print("-" * 34)
    for k, v in actual.items():
        tag = " *** >50K" if v > 50000 else ""
        print(f"  t_{k:<19s} {v:>10,d}{tag}")
    print("-" * 34)
    print(f"  {'TOTAL':<19s} {total:>10,d}")
    print(f"  {'AVERAGE':<19s} {avg:>10,.1f}")
    print()
    print(f"avg >= 5,000: {'OK' if avg >= 5000 else 'FAIL'}")
    print(f">= 2 tables > 50K: {'OK' if big >= 2 else 'FAIL'} ({big})")
    print(f"耗时: {elapsed:.1f} 秒")
    print(f"文件: {OUTPUT} ({size_mb:.1f} MB)")
    print()
    print("导入命令:")
    print("  1. mysql -u root -p < schema-v2.sql")
    print("  2. mysql -u root -p sms < generated_data.sql")


if __name__ == "__main__":
    main()
