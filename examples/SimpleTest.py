from precisely import assert_that
from precisely import equal_to


def test_assert_false():
    (assert_that)(False, (equal_to)(True))


def test_assert_true():
    (assert_that)(True, (equal_to)(False))


def test_assert_42_equals_47():
    (assert_that)(42, (equal_to)(47))


def test_assert_42_equals_42():
    (assert_that)(42, (equal_to)(42))
