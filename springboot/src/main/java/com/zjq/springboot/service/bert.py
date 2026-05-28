from transformers import BertTokenizer, BertModel
import torch

def calculate_similarity(sentence1, sentence2):
    # 加载预训练模型，可以替换合适的模型，如‘bert-base-chinese’，适合中文
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertModel.from_pretrained('bert-base-chinese')

    # 对句子进行tokenization和padding，并计算句子的嵌入表示
    encoded_inputs = tokenizer([sentence1, sentence2], padding=True, truncation=True, return_tensors='pt')
    with torch.no_grad():
        outputs = model(**encoded_inputs)
        sentence1_embeddings = outputs.last_hidden_state[0]  # 句子1的嵌入表示
        sentence2_embeddings = outputs.last_hidden_state[1]  # 句子2的嵌入表示

    # 计算余弦相似度
    similarity = torch.cosine_similarity(sentence1_embeddings.mean(dim=0), sentence2_embeddings.mean(dim=0), dim=0)
    return similarity.item()

sentence1 = input()
sentence2 = input()
print(calculate_similarity(sentence1, sentence2))