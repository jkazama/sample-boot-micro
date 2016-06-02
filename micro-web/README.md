micro-web
---

外部に公開する API フロントプロセスです。

- 基本的に処理は全て `micro-app` へ流します。
    - 流す対象は `micro-registry` から見つけ出します。
    - Ribbon を経由させてロードバランシングさせています。
- UI リソースも同梱させたい時は、 UI リソースのビルド先を `main/resources/static` 直下にしてください。
