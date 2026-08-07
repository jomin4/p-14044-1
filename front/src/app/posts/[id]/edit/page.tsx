"use client";

import { useParams, useRouter } from "next/navigation";

import usePost from "@/domain/post/hooks/usePost";
import withLogin from "@/global/auth/hoc/withLogin";

export default withLogin(function Page() {
  const router = useRouter();

  const { id: idStr } = useParams<{ id: string }>();
  const id = Number(idStr);

  const { post, modifyPost } = usePost(id);

  if (post == null) return <div>로딩중...</div>;

  const handleSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.currentTarget;

    const titleInput = form.elements.namedItem("title") as HTMLInputElement;
    const contentTextarea = form.elements.namedItem(
      "content",
    ) as HTMLTextAreaElement;

    titleInput.value = titleInput.value.trim();

    if (titleInput.value.length === 0) {
      alert("제목을 입력해주세요.");
      titleInput.focus();
      return;
    }

    contentTextarea.value = contentTextarea.value.trim();

    if (contentTextarea.value.length === 0) {
      alert("내용을 입력해주세요.");
      contentTextarea.focus();
      return;
    }

    modifyPost(post.id, titleInput.value, contentTextarea.value, (data) => {
      alert(data.msg);
      router.replace(`/posts/${id}`);
    });
  };

  return (
    <>
      <h1>{id}번 글 수정</h1>

      <form className="flex flex-col gap-2 p-2" onSubmit={handleSubmit}>
        <input
          className="border p-2 rounded"
          type="text"
          name="title"
          placeholder="제목"
          autoFocus
          defaultValue={post.title}
        />
        <textarea
          className="border p-2 rounded"
          name="content"
          placeholder="내용"
          defaultValue={post.content}
        />
        <button className="border p-2 rounded" type="submit">
          저장
        </button>
      </form>
    </>
  );
});
