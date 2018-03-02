package version_A;

public class Motivation {
  private short tiredMin = 0;
  private short tiredLess = 0;
  private short tiredCur = 0;
  private short tiredMore = 0;
  private short tiredMax = 0;
  private short hungryMin = 0;
  private short hungryLess = 0;
  private short hungryCur = 0;
  private short hungryMore = 0;
  private short hungryMax = 0;
  private short angryMin = 0;
  private short angryLess = 0;
  private short angryCur = 0;
  private short angryMore = 0;
  private short angryMax = 0;
  private short greedMin = 0;
  private short greedLess = 0;
  private short greedCur = 0;
  private short greedMore = 0;
  private short greedMax = 0;
  private short fearMin = 0;
  private short fearLess = 0;
  private short fearCur = 0;
  private short fearMore = 0;
  private short fearMax = 0;
  private short ego = 0;
  /* ~Goblin~
   * Tired:--
   * Hunger:---
   * Anger:--
   * Greed:-----
   * Fear:---
   */
  public int getTired() {
    return (tiredCur-tiredMin)/(tiredMax-tiredMin)*100;
  }
  public Motivation setTiredMin(short tiredMin) {
    this.tiredMin = tiredMin;
    return this;
  }
  public Motivation setTiredLess(short tiredLess) {
    this.tiredLess = tiredLess;
    return this;
  }
  public Motivation setTiredCur(short tiredCur) {
    this.tiredCur = tiredCur;
    return this;
  }
  public Motivation setTiredMore(short tiredMore) {
    this.tiredMore = tiredMore;
    return this;
  }
  public Motivation setTiredMax(short tiredMax) {
    this.tiredMax = tiredMax;
    return this;
  }
  /*
  public short getHungryMin() {
    return hungryMin;
  }
  public Motivation setHungryMin(short hungryMin) {
    this.hungryMin = hungryMin;
  }
  public short getHungryLess() {
    return hungryLess;
  }
  public Motivation setHungryLess(short hungryLess) {
    this.hungryLess = hungryLess;
  }
  public short getHungryCur() {
    return hungryCur;
  }
  public Motivation setHungryCur(short hungryCur) {
    this.hungryCur = hungryCur;
  }
  public short getHungryMore() {
    return hungryMore;
  }
  public Motivation setHungryMore(short hungryMore) {
    this.hungryMore = hungryMore;
  }
  public short getHungryMax() {
    return hungryMax;
  }
  public Motivation setHungryMax(short hungryMax) {
    this.hungryMax = hungryMax;
  }
  public short getAngryMin() {
    return angryMin;
  }
  public Motivation setAngryMin(short angryMin) {
    this.angryMin = angryMin;
  }
  public short getAngryLess() {
    return angryLess;
  }
  public Motivation setAngryLess(short angryLess) {
    this.angryLess = angryLess;
  }
  public short getAngryCur() {
    return angryCur;
  }
  public Motivation setAngryCur(short angryCur) {
    this.angryCur = angryCur;
  }
  public short getAngryMore() {
    return angryMore;
  }
  public Motivation setAngryMore(short angryMore) {
    this.angryMore = angryMore;
  }
  public short getAngryMax() {
    return angryMax;
  }
  public Motivation setAngryMax(short angryMax) {
    this.angryMax = angryMax;
  }
  public short getGreedMin() {
    return greedMin;
  }
  public Motivation setGreedMin(short greedMin) {
    this.greedMin = greedMin;
  }
  public short getGreedLess() {
    return greedLess;
  }
  public Motivation setGreedLess(short greedLess) {
    this.greedLess = greedLess;
  }
  public short getGreedCur() {
    return greedCur;
  }
  public Motivation setGreedCur(short greedCur) {
    this.greedCur = greedCur;
  }
  public short getGreedMore() {
    return greedMore;
  }
  public Motivation setGreedMore(short greedMore) {
    this.greedMore = greedMore;
  }
  public short getGreedMax() {
    return greedMax;
  }
  public Motivation setGreedMax(short greedMax) {
    this.greedMax = greedMax;
  }
  public short getFearMin() {
    return fearMin;
  }
  public Motivation setFearMin(short fearMin) {
    this.fearMin = fearMin;
  }
  public short getFearLess() {
    return fearLess;
  }
  public Motivation setFearLess(short fearLess) {
    this.fearLess = fearLess;
  }
  public short getFearCur() {
    return fearCur;
  }
  public Motivation setFearCur(short fearCur) {
    this.fearCur = fearCur;
  }
  public short getFearMore() {
    return fearMore;
  }
  public Motivation setFearMore(short fearMore) {
    this.fearMore = fearMore;
  }
  public short getFearMax() {
    return fearMax;
  }
  public Motivation setFearMax(short fearMax) {
    this.fearMax = fearMax;
  }
*/
}
